from __future__ import absolute_import
from __future__ import division
from __future__ import print_function

import time

import numpy as np
from PIL import Image
import tensorflow as tf # TF2

import json

from io import BytesIO
import base64

from flask import Flask
from flask import request

app = Flask(__name__)

def load_labels(filename):
  with open(filename, 'r') as f:
    return [line.strip() for line in f.readlines()]
@app.route("/diagnose", methods=["POST"])
def diagnose():
  req = request.get_json()
  interpreter = tf.lite.Interpreter(model_path="sd128-b7.tflite")
  interpreter.allocate_tensors()

  input_details = interpreter.get_input_details()
  output_details = interpreter.get_output_details()

  # check the type of the input tensor
  floating_model = input_details[0]['dtype'] == np.float32

  # NxHxWxC, H:1, W:2
  height = input_details[0]['shape'][1]
  width = input_details[0]['shape'][2]
  img = Image.open(BytesIO(base64.b64decode(req['img']))).resize((width, height))

  # add N dim
  input_data = np.expand_dims(img, axis=0)

  if floating_model:
    input_data = (np.float32(input_data) - 127.5) / 127.5

  interpreter.set_tensor(input_details[0]['index'], input_data)

  start_time = time.time()
  interpreter.invoke()
  stop_time = time.time()

  output_data = interpreter.get_tensor(output_details[0]['index'])
  results = np.squeeze(output_data)

  top_k = results.argsort()[-5:][::-1]
  labels = load_labels("sd128-b7-labels.txt")
  r = {'result': [], 'labels': []}
  for i in top_k:
    if floating_model:
      r['result'].append(results[i].item())
      r['labels'].append(labels[i])
      print('{:08.6f}: {}'.format(float(results[i]), labels[i]) + "\n")
    else:
      print('{:08.6f}: {}'.format(float(results[i] / 255.0), labels[i]))

  return json.dumps(r)

if __name__ == '__main__':
    app.run(host="0.0.0.0")