Hi!

Here I want to write notes what architectural and design decisions I made and why.

Architecture:
- Split the code into 3 layers(data, domain, ui). I wouldn't do that in the production app. 
    I'd rather use feature-modules. In this relatively small app - it would be overkill, so 3 layers it is.
    There is not much stuff on the domain layer, because I kind of against of having proxy UseCases. 
    I prefer injecting repositories directly to ViewModel if there is no need of UseCase(business logic) 

- DI - Hilt since it's the most popular DI framework at the moment plus built on top of Dagger with less boilerplate
- UI layer -- MVVM. also because of the popularity of the approach.

Design:
- CameraX output as bitmap
    there must be a better way to provide a frame to FaceDetector, but I've tried ImageAnalyzer.analyse, ImageCapture.captureImage
    and for a reasonable amount of time couldn't make it work. So I decided to keep it simple and working and provide a bitmap
    from CameraPreview. In my opinion it would be enough for MVP, performance improvements could me another iteration of development.

- FRAMES/SECONDS inconsistency
    I noticed that on the session summary screen all results are in seconds. 
    Though if count a real time it could be a possibility that total duration would be not aligned with face recognition TIMES,
    because we analyze the face only once per second.
    So in order to avoid an inconsistency there -- I decided to not save real time of starting/ending the session and
    consider amount of FRAMES as total duration of the session in SECONDS

- Face/No face detected images have a different size (60px vs 70px). Green smile has padding 10px and red one doesn't. 
    You could notice them jumping a little bit on the 2 screen, but I decided not to bother you or your designer to fix it.
    Changing size in SVG or ImageView itself doesn't help since the icons built differently(vector images)

- Looks like ':' is forbidden for file names since the system drops them automatically, so I made the date format like: yyyy-MM-dd_HHmmss
