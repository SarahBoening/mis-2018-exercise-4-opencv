Red nose:

1. Detect face using OpenCV and a haarcascade classifier
2. For every detected face calculate the following:
	-  The center point of the nose (see sources): x = middle of the face (face.x + face.width/2), y =  one third of the face (face.y + face.height*0.66). Since this proved to be too low for some more oval faces during testing, the value was set to 0.6 based on trial and error.
	- radius of nose (see sources): One fifth of the width of the face (radius = face.width/5). However, this turned out to be too wide during testing, so it was set to 1/6.5 based on trial and error ( radius = face.width/6.5).
4. Draw red circle


Please note: the used classifier only detects frontal faces, so faces turned a bit to the side may not be detected.

Sources for nose position and width:
- http://www.lavedo.com/laser/images/ssc_r4.jpg
- https://design.tutsplus.com/tutorials/human-anatomy-fundamentals-basics-of-the-face--cms-20417

Sources for the face detection code:
- https://docs.opencv.org/3.4.1/d7/d8b/tutorial_py_face_detection.html
- https://github.com/opencv/opencv/blob/master/samples/android/face-detection/src/org/opencv/samples/facedetect/FdActivity.java