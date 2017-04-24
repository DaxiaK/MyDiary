#  MyDiary

## Summary

I like Makoto Shinkai's (新海 誠) movies very much! This project is just trying to create the app - "My Diary" in "your name." on Android. In this movie, "My Diary" is an important app between Taki(瀧) and Mitsuha(三葉). As a fan, I think maybe I can implement this app. If you also like "your name.", please join me to finish the "My Diary", and let it become real.

This project will need :
* Android developer
* GUI designer
* Japanese/English translation 

This project has be re-created in 2016-11-04 because there were some personal infos in the old git repository.

So if you have already starred or forked the repo, you can do it again, thanks :)

## Why is there only the Android version?

In fact, Android version is more difficult than iOS, because this app is running on iOS in the movie. But I will only create the Android version for now because:

1. I don't have a mac for now :P
2. Android apks are easy to release.

So, maybe I will try to create an iOS version after this app is running.


## Functionality

 **Working:**
* Add topic
* Delete topic
* Show diary list
* Add diary
* Delete diary
* Change date in calendar page (page curl effect not ready)
* Add mood in diary
* Add weather in diary
* Add location in diary
* Basic theme
* Allow edit diary
* Add memo
* Support JP
* Create contacts
* Optimized diary page viewer 
* Add photo in diary
* Allow changing diary time
* Main Setting page
* More themes, More profiles
* Add profile picture
* Add password
* Allow order for topic, memo
* page curl effect
* import & export DB(Alpha) (flow finish) 
* Search function
* OOBE
* Save diary automatically
* Photo overview
**To do:**

This is the list of functionality I will implement, ordered by priority.

1. Modfiy location
2. Code refactoring
3. Diary widget
4. Google account backup & including FireBase
5. UI review & support xxxHpdi \
6. Implement contacts search
7. Support Chinese sort
8. Modify contacts detail (beta)
9. Music
10. web block
11. Tags
12. load contacts from phone
13. Add bookmark for diary
14. Add music in diary
15. Support Chinese sort
16. Support JP sort

Coding & Designing:

1. Add mistake-proofing

**pending**

1. Add web block in diary ( some memory issue)


**Translation**

Thanks for someone help me to translate this app.
Now the language percentage is:

* English - 100 %
* 日本語  - 80%
* 繁體中文 - 100%
* 简体中文 - 100%
* 한국어 - 80%
* ภาษาไทย  - 99%
* Français  - 99%
* Español  - 80%



## Known issues

* Requesting permissions dialog fail on HTC D820t [API 23], HUAWEI honor 7i [API 22]
* Writing diary with location will crash on some Chineses device.

## Use case

Because the DVD/BD aren't released yet, the use case is made from my memory and network information.
If you have any idea or find some functionality that I miss, you can tell me or update the use case in **/UML/MyDiary.mdj**  by [starUML](http://staruml.io/):

![](/screenshot/usercase.png) 


## screenshot

![](/screenshot/s_0.png) 
![](/screenshot/s_1.png) 
![](/screenshot/s_2.png) 
![](/screenshot/s_3.png)
![](/screenshot/s_4.png)
![](/screenshot/s_5.png)
![](/screenshot/s_6.png)


# Apk
[![](/screenshot/google-play-badge.png) ](https://play.google.com/store/apps/details?id=com.kiminonawa.mydiary)


## License

For MIT license, I encourage more user or developer who like this movie can get some idea to do best project.
If you want to create a new project based on this app, please create it by new feature, new idea or new design.

I denounce someone who only copies this project and release the same version onto Google play.

-----------------------------------------------

**The MIT License (MIT)**
Copyright (c) 2016 Daxia

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


## Contact me

Daxia (guava.bala@gmail.com)

P.S. I spend too much time anwering the same questions everyday (Ex.page effect, google account, password).
I should focus the coding again.

So if you send some duplicate question for me , forgive me I will ignore it at sometimes. 
Most question you can find it in "Functionality".

