'''
Created on Aug 17, 2020

@author: Richard.Wang
'''
import glob
import os

targetfolder = "C:\\Temp\\test\\"



# Create folder if not exist
if not os.path.exists(targetfolder):
    os.makedirs(targetfolder)

# List all files in a folder    
targetFiles = []
for file in glob.glob(targetfolder + "*.TIF"):
    targetFiles.append(file)
    
for oneFile in targetFiles:
    print(oneFile)