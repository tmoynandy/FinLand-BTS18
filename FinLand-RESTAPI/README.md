# FinLand-API
API for [FinLand android app](https://github.com/morninigstar/FinLand) made using [Flask](http://flask.pocoo.org/). HackerEarth [BTS Hackathon](https://www.hackerearth.com/sprints/bts-global-hackathon-asia/)


# Setup
Make sure you have `python3.6` installed in your machine before you continue. For windows users, the path to `python3.6` must be added to the system environment variable `PATH`.  
Install virtualenv in python. `pip3 install virtualenv`

## Create a virtual environment
### Linux
```bash
$ virtualenv -p python3 venv
```
This will create a virtual environment named `venv` with `python3` as its interpreter.

## Windows
If you have multiple versions of python installed and added to `PATH`, find out the path for `python3.6`.  
```cmd
> where python
C:\Python27\python.exe
C:\Users\deepd\AppData\Local\Programs\Python\Python37-32\python.exe
C:\Users\deepd\AppData\Local\Programs\Python\Python36\python.exe
```
You will get an output of all the python execuables that are added to `PATH`. For example I have `python2.7`, `python3.6` and `python3.7` installed.

Use the path of `python3.6` to create the virtual environment.
```cmd
> virtualenv -p C:\Users\deepd\AppData\Local\Programs\Python\Python36\python.exe venv
```

## Activating the virtualenv
### Linux
```bash
$ source venv/bin/activate
(venv) ...$ 
```
### Windows
```cmd
> venv\Scripts\activate
(venv) ...> 
```

## Installing requirements
You can either install requirements from the [requirements.txt](/requirements.txt) file but using the command  
```
pip install -r requirements.txt
```

## Running the API
Run the [api.py](/api.py) file to start the API server.  
```
python api.py
```

## API Response Exmples
On success
```json
{
  "land-type": "Herbaceous Vegetation",
  "probability": "100.0 %",
  "status": "success",
  "status-code": 1,
  "time-taken": 1.6259400844573975
}
```
On failure
```json
{
  "land-type": null,
  "probability": null,
  "status": "failure",
  "status-code": 0,
  "time-taken": -1
}
```


## Team c_of_pythons
1. [Diptangsu Goswami](https://github.com/diptangsu)
2. [Tanumoy Nandi](https://github.com/tmoynandy)
3. [Sujoy Dutta](https://github.com/Sujoydatta26)
4. [Koustav Chanda](https://github.com/KoustavCode)
