# Trell Android Assignment - Yasin Ashraf

### Language : Kotlin

### Architecture

MVVM Architecture

### About

Using Architecture Components - LiveData & ViewModel  
Jetpack Navigation Component  
Data binding  
FFMpeg library for compressing

### Walk-through

App Icon is letter 'Tr' in black bg 

Screen 1  : Add File Screen

Shows a button to select file via File Picker. Selecting video moves to Screen 2

Screen 2 : 
Shows Selected Video at the top
Video starts playing on resume

EditText at the centre to type the bit rate

Compress video button compresses video and saves file in "CompressedVideo" folder in internal storage

Navigates to Screen 3 on completion of compressing.   
Clicking on compress button navigates to Screen 3 if the file is already compressed

Screen 3 :
Shows compressed video with play / pause button

