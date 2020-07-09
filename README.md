# Android-PinDotView

Custom views to speed up creating custom PIN screen in your app

## Demo
### NumberDialView & PinDotView

<img src="screen-1.png" alt="screen-1" width="320" height="640">

<img src="demo.gif" alt="screen-2" >

### PinView

<img src="screen-2.png" alt="screen-2" width="320" height="640">

## Features
- Easy to implement
- Exposing only necessary callbacks for ease of use
- Views work together out of the box
- Highly customizable

## Usage
### Implementation
`dependencies {
    implementation 'com.github.QusionDev:android-pin-dot-view:x.x.x'
}`

### Layout
```
<com.qusion.lib_pindotview.PinDotView
        android:id="@+id/pinDotView"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="60dp"
        app:currentDotGlareSize="28dp"
        app:dotSpacing="70dp"
        app:idleDotSize="10dp"
        app:layout_constraintBottom_toTopOf="@id/numberDialView"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:pinLength="4" />
```
supports `pinLength`, `dotSpacing`, `idleDotSize`, `idleDotColor`, `currentDotGlareSize`, `currentDotGlareColor`, `passedDotSize`, `passedDotColor` params
 
```
<com.qusion.lib_pindotview.NumberDialView
        android:id="@+id/numberDialView"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="400dp"
        app:horizontalDelimiterWidth="1dp"
        app:textSize="26sp"
        app:textStyle="bold"
        app:verticalDelimiterWidth="1dp" />
```
supports `textSize`, `textColor`, `textStyle`, `backgroundColor`, `delimiterColor`, `biometricsTint`, `verticalDelimiterWidth`, `horizontalDelimiterWidth` params

``` 
<com.qusion.lib_pindotview.PinView
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintBottom_toTopOf="@id/numberDialView"
        app:pinLength="4"
        android:textSize="28sp"
        android:layout_marginBottom="60dp"
        android:layout_marginStart="60dp"
        android:layout_marginEnd="60dp"
        app:digitSpacing="40dp" />
```
supports `pinLength`, `textColor`, `idleColor`, `activeColor`, `digitSpacing` params

### Activity | Fragment
```
pinDotView.numberDialView = numberDialView
pinDotView.setOnCompletedListener { pin ->
    // returns completed PIN code
}
numberDialView.setOnBiometricsClickedListener {
    // Biometrics button clicked
}
```

```
// Can be used with soft input -> don't assign NumberDialView here and be sure to specify inputType in XML
pinView.numberDialView = numberDialView
pinView.setOnCompletedListener { pin ->
    // returns completed PIN code
}
```

## License
```
MIT License
Copyright (c) 2020 QusionDev

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

        
