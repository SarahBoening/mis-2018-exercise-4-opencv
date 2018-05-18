*Red nose*

1. Detect face using OpenCV and a haarcascade
2. Detect nose using OpenCV and a haarcascade (Found at https://raw.githubusercontent.com/opencv/opencv_contrib/master/modules/face/data/cascades/haarcascade_mcs_nose.xml)
3. For every nose detected within the detected face calculate the following:
	- Center point of nose: x+ (((x + width)-x)/2) = width/2+x, y analogous
	- radius for nose: {width, height}/ 2
4. Draw red circle
This calculates the center and length from the center to one side. Since not all noses have the same shape, some could be bigger then the diameter of this circle. Hence the radius is magnified by a third.