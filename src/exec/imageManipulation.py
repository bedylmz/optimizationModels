from PIL import Image
from pathlib import Path

def imageManipulator(path, size ,classIndex):
    pngfiles  = list(Path(path).rglob("*.png"))  # Finds all .png files in the directory
    jpgfiles  = list(Path(path).rglob("*.jpg"))
    jpegfiles = list(Path(path).rglob("*.jpeg"))
    giffiles  = list(Path(path).rglob("*.gif"))
    bmpfiles  = list(Path(path).rglob("*.bmp"))

    files = pngfiles+jpgfiles+jpegfiles+giffiles+bmpfiles

    for index, file in enumerate(files):
        with Image.open(file) as im:
            #convert into grayscale
            grayscaleImg = im.convert("L")

            resizedImg = grayscaleImg.resize((size,size))
            
            resizedImg.save("img\\class"+str(classIndex)+"\\"+str(index)+".bmp")
path1 = ""
path2 = ""
size = 0
with open("img\\photo.dat", "r") as file:
    for index, line in enumerate(file):
        print(line)
        if(index == 1 or index == 3):
            path = line.replace('\\', "\\\\")
            path = path.strip()
            if(index == 1):
                path1 = path
            else:
                path2 = path
        if(index == 6):
            size = int(line.strip())
            print(size)

imageManipulator(path1, size, 1)
imageManipulator(path2, size, 2)