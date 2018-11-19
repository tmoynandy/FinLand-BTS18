import os

from flask import Flask
from flask import jsonify
from flask import request
from flask import render_template

import keras
from keras import backend as k
from keras.models import load_model

import numpy as np
import tensorflow as tf

import PIL.Image
import base64
from time import time


app = Flask(__name__)

UPLOAD_FOLDER = os.path.basename('uploads')
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

loaded_model = None
graph = None

TYPES = {
    0: 'Sea Lake',
    1: 'River',
    2: 'Residential',
    3: 'Permanent Crop',
    4: 'Pasture',
    5: 'Indusrial',
    6: 'Highway',
    7: 'Herbaceous Vegetation',
    8: 'Forest',
    9: 'Annual Crop',
}

STATUS = {
    0: 'failure',
    1: 'success',
}

def load_model_file():
    global loaded_model, graph
    graph = tf.get_default_graph()
    loaded_model = load_model('trained-model/land_predict.h5')


def resize_image(img, image_path):
    basewidth = 64
    width, height = img.size

    min_ = min(img.size)
    v = min_ // 64
    dimens = 64 * v

    h = (height - dimens) // 2
    w = (width - dimens) // 2

    cropped_img = img.crop((w, h, dimens + w, dimens + h))

    wpercent = (basewidth / float(cropped_img.size[0]))
    hsize = int((float(cropped_img.size[1]) * float(wpercent)))
    cropped_img = cropped_img.resize((basewidth, hsize), PIL.Image.ANTIALIAS)
    
    cropped_img.save(image_path)


def get_prediction(image_path):
    global loaded_model

    PIL_image = PIL.Image.open(image_path)
    image_array = np.array(PIL_image)

    if image_array.shape != (64, 64, 3):
        resize_image(PIL_image, image_path)
        PIL_image = PIL.Image.open(image_path)
        image_array = np.array(PIL_image)
        
    image_dims = np.expand_dims(image_array, axis=0)
    
    prediction = None
    with graph.as_default():
        try:
            prediction = loaded_model.predict(image_dims)
        except:
            print('Exception')
            return None

    index = prediction[0].argmax()
    probability = prediction[0][index] * 100

    return {
        'probability': f'{probability:.4} %',
        'land-type': TYPES[index],
    }



def get_file_name():
    file_number = 0
    def inner():
        nonlocal file_number
        file_number += 1
        return f'image-{file_number}.png'
    return inner
get_file_name = get_file_name()


@app.route('/upload', methods=['POST'])
def upload():
    '''POST /upload
    POST image string encoded in base64
    '''
    file_name = get_file_name()
    file_path = os.path.join(app.config['UPLOAD_FOLDER'], file_name)

    encoded_image = request.form['image']
    decoded_image = base64.b64decode(encoded_image)

    image_file = open(file_path, "wb")
    image_file.write(decoded_image)
    image_file.close()

    start = time()
    prediction = get_prediction(file_path)
    end = time()
    duration = end - start
    status_code = 1

    if prediction is None:
        status_code = 0
        duration = -1
        prediction = {
            'probability': None,
            'land-type': None,
        }

    response = {
        'status-code': status_code,
        'status': STATUS[status_code],
        'time-taken': duration,
    }
    response.update(prediction)

    print(response)
    return jsonify(response)
 

@app.route('/')
def index():
    return 'Server Running'


if __name__ == '__main__':
    load_model_file()
    app.run(debug=True, host='0.0.0.0', port=80)
