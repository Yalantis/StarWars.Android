# StarWars.Android

[![License](http://img.shields.io/badge/license-MIT-green.svg?style=flat)](https://github.com/Yalantis/Side-Menu.iOS/blob/master/LICENSE)
[![Yalantis](https://raw.githubusercontent.com/Yalantis/PullToRefresh/develop/PullToRefreshDemo/Resources/badge_dark.png)](https://yalantis.com/?utm_source=github)

This component implements transition animation to crumble view into tiny pieces.



<img src="https://yalantis.com/media/content/ckeditor/2015/10/20/star_wars-shot.gif" />
<br>Check this <a href="https://dribbble.com/shots/2109991-Star-Wars-App-concept">project on dribbble</a>.

Also, read how it was done in [our blog](https://yalantis.com/blog/)

##Requirements
- Android SDK 15+
- OpenGL ES 2.0+

##Usage

1. Wrap your fragment or activity main view in TilesFrameLayout:

```
<com.yalantis.starwars.TilesFrameLayout
  android:id="@+id/tiles_frame_layout"
  android:layout_height="match_parent"
  android:layout_width="match_parent"
  app:sw_animationDuration="1500"
  app:sw_numberOfTilesX="35">

  <!-- Your views go here -->
     
</com.yalantis.starwars.TilesFrameLayout>
```


2. Adjust animation with these parameters:

```app:sw_animationDuration``` – duration in milliseconds
```app:sw_numberOfTilesX``` –  the number of square tiles the plane is tessellated into broadwise

```
mTilesFrameLayout = (TilesFrameLayout) findViewById(R.id.tiles_frame);
mTilesFrameLayout.setOnAnimationFinishedListener(this);
```



3. In your activity or fragment’s onPause() and onResume() it’s important to call the corresponding methods:
```
@Override
public void onResume() {
    super.onResume();
    mTilesFrameLayout.onResume();
}

@Override
public void onPause() {
    super.onPause();
    mTilesFrameLayout.onPause();
}
```

4. To start the animation simply call:

```
mTilesFrameLayout.startAnimation();
```

5. Your callback will be called when the animation ends:

```
@Override
public void onAnimationFinished() {
   // Hide or remove your view/fragment/activity here
}
```

Have fun! :)

#### Let us know!

We’d be really happy if you sent us links to your projects where you use our component. Just send an email to github@yalantis.com And do let us know if you have any questions or suggestion regarding the animation. 

P.S. We’re going to publish more awesomeness wrapped in code and a tutorial on how to make UI for iOS (Android) better than better. Stay tuned!

## License

	The MIT License (MIT)

	Copyright © 2015 Yalantis

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.

