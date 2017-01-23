#MyDiary 

## Summary

I like Makoto Shinkai's (新海 誠) movies very much! This project is just trying to create the app - "My Diary" in "your name." on Android. In this movie, "My Diary" is an important app between Taki(瀧) and Mitsuha(三葉). As a fan, I think maybe I can implement this app. If you also like "your name.", please join me to finish the "My Diary",and let it become real.

This project will need :
* Android developer
* GUI designer
* Japanese/English translation 

This project is created again in 2016-11-04. Because it have some my personal info in old git repository.

So if you have already a starred or forked the repo,  you can do it again, thanks :)

## Why is there only Android version?

In fact, Android version is more difficult than iOS, because this app is running on iOS in the movie. But I will only create the Android version for now because:

1. I don't have any mac now :P 
2. Android apk is easy to release.

so, maybe I will try to create an iOS version after this app is running.


## Functionality

 **Work:**
* Add topic
* Delete topic
* Show diary list
* Add diary
* Delete diary
* Change date in calendar page (page curl effect not ready)
* Add  mood in diary
* Add  weather in diary 
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
* More theme , More profile
* Add profile picture
* Add password

**Not Work:**

This list is what functionality I will implement , the number is the sequence for coding.

1. Allow order for topic , memo (Memo work)
2. import & export DB(Alpha) (flow finish) 
3. Search function
4. page curl effect
5. OOBE
6. Code refactoring
7. Google account backup & including FireBase
8. UI review & support xxxHpdi \
9. Implments search
10. Support Chinese sort
11. Modify contacts detail (beta)
12. Tags
13. load contacts from phone
14. Add bookmark for diary
15. Add music in diary
16. Support Chinese sort
17. Supprot JP sort

Coding & Designing:

1. Add mistake-proofing

**pending**

1. Add web block in diary ( some memory issue)


**Plan**

Some function will be implemented later ,but some user is asking about them.
If you want to help this project , you can select them first and let me know:

1. page curl effect in calendar(ref: [1](https://github.com/harism/android_page_curl) [2](http://blog.csdn.net/aigestudio/article/details/42678541) )
2. Implments search in MainActivity , ContactsActivity
3. load contacts from phone
4. Add border and radius in all dialogfragment 

## known issue

* requesting permissions dialog fail on HTC D820t [API 23], HUAWEI honor 7i [API 22]
* Writeing diary with location will crash on some China device. 

## Use case

Because DVD/BD aren't released yet, so the use case is made from my memory and network information. 
If you have any idea or find some functionality that I miss, you can tell me or update the use case in **/UML/MyDiary.mdj**  by [starUML](http://staruml.io/):

![](/screenshot/usercase.png) 


## Ui flow

Designing.

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

For MIT licenese , I encourage more user or developer who like this moive can get some idea to do best project. 
If you want to create a new project base on this app , please create it by new feature ,new idea or new design.

I denounce someone who only copies this project and release the same version into Google play.

-----------------------------------------------

**The MIT License (MIT)**
Copyright (c) 2016 Daxia

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.


## Contact me

Daxia (guava.bala@gmail.com)

P.S. I spend too much time on same questions everyday (Ex.page effect, google account ,password). 
I shold focus the coding again.

So if you send some duplicate question for me , forgive me I will ignore it at sometimes. 
Most question you can find it in "Functionality".

