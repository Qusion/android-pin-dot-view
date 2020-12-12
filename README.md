<p>
<img align="left" width="400" height="400" src="demo.gif" alt="demo" />
<p><h1 align="left">Android-PinDotView</h1></p>
<p>Set of views to speed up creating custom PIN screen in your app </p>
</p>

</br></br></br></br></br></br></br></br></br></br></br></br>

## Demo
<table cellspacing="0" cellpadding="0" >
    <tr>
        <th width="700px" bgcolor="transparent"><h3> NumberDialView & PinDotView </h3></th>
        <th width="700px"><h3> PinView </h3></th>
    </tr>
    <tr>
        <th width="700px"><img src="screen-1.png" alt="screen-1" width="320" height="640" align="center" /></th>
        <th width="700px"><img src="screen-2.png" alt="screen-2" width="320" height="640" align="center" /></th>
    </tr>
</table>


## Features
- Easy to implement
- Exposing only necessary callbacks for ease of use
- Customizable using themes

## Usage
### Implementation
`dependencies {
    implementation 'com.github.Qusion:android-pin-dot-view:0.0.2'
}`

### Layout
```
<com.qusion.lib_pindotview.PinDotView
    android:id="@+id/pinDotView"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    app:pin_length="4"
    android:theme="@style/Widget.NumberDialView"
    app:layout_constraintTop_toBottomOf="@id/center_guideline"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent" />
```
supports `biometrics_button_src`, `back_button_src`, `forgot_button_text`, `pin_length` params

```
<com.qusion.lib_pindotview.PinView
    android:id="@+id/pinView"
    android:layout_width="match_parent"
    android:layout_height="0dp"
    app:pin_length="4"
    android:theme="@style/Widget.NumberDialView"
    app:layout_constraintTop_toBottomOf="@id/center_guideline"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintLeft_toLeftOf="parent" />
```
supports `biometrics_button_src`, `back_button_src`, `forgot_button_text`, `pin_length` params


### Activity | Fragment
```
pinDotView.setOnCompletedListener { pin ->
    // In case of incorrect pin we can show error animation when using PinDotView
    // clearPin gives you the option to reset on error
    // pinDotView.showErrorAnimation(clearPin = true)
}

pinDotView.setOnBiometricsButtonClickedListener {
    // Biometrics button clicked
}

pinDotView.setOnForgotButtonClickedListener {
    // Forgot button clicked
}
```


### Styling
```
<style name="ThemeOverlay.PinDotView" parent="">
    <item name="colorPrimary">@color/color_aqua</item> // Tints biometric icon and active elements of pin input
    <item name="colorOnSurface">@color/color_white</item> // Changes color of idle elements of pin input and keyboard delimiters
    // + regular text styling
</style>
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

        
