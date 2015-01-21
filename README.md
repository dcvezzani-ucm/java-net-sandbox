RESTful Box client
=====

Recently, my customer has requested a Java-based RESTful client to make backend requests to the Box file storage service.

Initially, I thought that I would use the java.net library to accomplish this.  There are some bugs, apparently, with cookie management that I haven't resolved yet.  Since I am dealing with a time constraint, I looked around for the next best option.

Apache's HttpClient does a pretty good job.  I was able to achieve successful authentication.  I am concerned that there are warnings all over the HttpClient code that the library is not thread safe.  If we do end up going with HttpClient, we will need to accommodate for HttpClient's lack of thread safe-ness.

Prerequisites
------

* Java 1.7 (http://www.oracle.com/technetwork/java/javase/downloads/jdk7-downloads-1880260.html)
* Eclipse IDE for Java Developers (Version: Luna Service Release 1 (4.4.1); Build id: 20140925-1800) (http://www.eclipse.org/downloads/packages/eclipse-ide-java-developers/lunasr1)
* Maven 3.1.1 (http://maven.apache.org/download.cgi)

Eclipse and Maven can be used together to provide a rich environment in which to develop Java applications.  Eclipse with Maven plugins can be used by those who would wish to use a graphical interface.  Maven from the command line can be used by those who enjoy the speed in a terminal window.  Developers with either preference can co-exist well in a code repository thanks to Maven.

Make sure the following Eclipse plugins are installed.

Help > Eclipse Marketplace...

Eclipse Marketplace
* AnyEdit Tools (workset management)
* EGit - Git Team Provider (git support)
* JAutodoc (javadoc convenience)
* JVM Monitor (profiler)
* Maven (Java EE) Integration for Eclipse WTP (Luna) 1.1.0
* Vrapper (Vim) (Vim support)

Yoxos Marketplace
* Vrapper - Surround Plugin (Optional)

Assumptions
-------

Service will be run in a Unix (or Unix-like) environment.  I use Mac OS.  A different OS can most certainly be used, but slight modifications to the following instructions will likely be necessary.

Prepare for use
-----

RESTful service

Create a Box enterprise account.  Create a Box application and generate a set of ```client_id``` and ```client_secret``` keys.

Set up the Java-based web client (implemented with Apache's HttpClient library).

1. git clone https://github.com/dcvezzani-ucm/java-net-sandbox.git
1. cp application.properties.sample application.properties
1. edit application.properties; supply the necessary information
1. start Eclipse
1. create or select a workspace in which to place the project
1. in terminal window, go to directory of selected workspace
1. import "Existing Projects into Workspace"

Start
----

1. in "Package Explorer" pane, select "user-inactivate-client" and expand
1. locate and open "src/test/java/edu/ucmerced/box/client/JoyRide.java"
1. "Run As" "JUnit Test"

The results should include something like the following at the bottom of the console pane/tab.

```
2015-01-12_09:00:29.453 DEBUG e.u.box.client.BoxServiceClient - Created query: ?response_type=code&client_id=d76f7ks03abue3jg72k0zt1d82w14y35
2015-01-12_09:00:30.938 DEBUG e.u.box.client.BoxServiceClient - Status: HTTP/1.1 200 OK
2015-01-12_09:00:30.971 DEBUG e.u.box.client.BoxServiceClient - Response content: <!DOCTYPE html><html lang="en-US"><head><title></title><meta name="viewport" content="initial-scale = 1.0,maximum-scale = 1.0" /><link rel="stylesheet" href="https://e1.boxcdn.net/_assets/css/section_templ_webviews_login_oauth_login-vJ_8-s.css"/><link rel="stylesheet" href="https://e1.boxcdn.net/_assets/css/section_templ_webviews_login_login-Nc4rCh.css"/><link rel="stylesheet" href="https://e1.boxcdn.net/_assets/css/webviews/webviews_image_ios-_ORMpz.css"/><script type="text/javascript">var request_token = '0d27ae47e...
2015-01-12_09:00:30.973 DEBUG e.u.box.client.BoxServiceClient - 8387 bytes were written to file, request_token.txt

2015-01-12_09:00:30.973 DEBUG e.u.box.client.BoxServiceClient - Request Method: GET
2015-01-12_09:00:30.973 DEBUG e.u.box.client.BoxServiceClient - Name : Server, Value: ATS
2015-01-12_09:00:30.973 DEBUG e.u.box.client.BoxServiceClient - Name : Date, Value: Mon, 12 Jan 2015 16:59:15 GMT
2015-01-12_09:00:30.974 DEBUG e.u.box.client.BoxServiceClient - Name : Content-Type, Value: text/html; charset=UTF-8
2015-01-12_09:00:30.974 DEBUG e.u.box.client.BoxServiceClient - Name : Set-Cookie, Value: z=b637bhib7qqbcrn7pptf3rucf3; path=/; domain=.box.com; secure; HttpOnly
2015-01-12_09:00:30.974 DEBUG e.u.box.client.BoxServiceClient - Name : Expires, Value: Thu, 19 Nov 1981 08:52:00 GMT
2015-01-12_09:00:30.974 DEBUG e.u.box.client.BoxServiceClient - Name : Cache-Control, Value: no-store, no-cache, must-revalidate, post-check=0, pre-check=0
2015-01-12_09:00:30.974 DEBUG e.u.box.client.BoxServiceClient - Name : Pragma, Value: no-cache
2015-01-12_09:00:30.974 DEBUG e.u.box.client.BoxServiceClient - Name : Set-Cookie, Value: box_visitor_id=54b3fd6346de11.57293055; expires=Tue, 12-Jan-2016 16:59:15 GMT; path=/; domain=.box.com
2015-01-12_09:00:30.975 DEBUG e.u.box.client.BoxServiceClient - Name : Set-Cookie, Value: bv=OPS-38455; expires=Mon, 19-Jan-2015 16:59:15 GMT; path=/; domain=.box.com; secure
2015-01-12_09:00:30.975 DEBUG e.u.box.client.BoxServiceClient - Name : Set-Cookie, Value: cn=69; expires=Tue, 12-Jan-2016 16:59:15 GMT; path=/; domain=.box.com; secure
2015-01-12_09:00:30.975 DEBUG e.u.box.client.BoxServiceClient - Name : Set-Cookie, Value: presentation=desktop; path=/; domain=.box.com; secure
2015-01-12_09:00:30.975 DEBUG e.u.box.client.BoxServiceClient - Name : Vary, Value: Accept-Encoding
2015-01-12_09:00:30.975 DEBUG e.u.box.client.BoxServiceClient - Name : Age, Value: 0
2015-01-12_09:00:30.975 DEBUG e.u.box.client.BoxServiceClient - Name : Transfer-Encoding, Value: chunked
2015-01-12_09:00:30.976 DEBUG e.u.box.client.BoxServiceClient - Name : Connection, Value: keep-alive
2015-01-12_09:00:30.988 DEBUG e.u.box.client.BoxServiceClient - request_token has been cached (...646f5)
2015-01-12_09:00:30.988 DEBUG edu.ucmerced.box.client.JoyRide - request_token: ...646f5

2015-01-12_09:00:30.989 DEBUG e.u.box.client.BoxServiceClient - Created query: ?response_type=code&client_id=d76f7ks03abue3jg72k0zt1d82w14y35
2015-01-12_09:00:31.655 DEBUG e.u.box.client.BoxServiceClient - Request Method: POST
2015-01-12_09:00:31.655 DEBUG e.u.box.client.BoxServiceClient - Name : Server, Value: ATS
2015-01-12_09:00:31.656 DEBUG e.u.box.client.BoxServiceClient - Name : Date, Value: Mon, 12 Jan 2015 16:59:16 GMT
2015-01-12_09:00:31.656 DEBUG e.u.box.client.BoxServiceClient - Name : Content-Type, Value: text/html; charset=UTF-8
2015-01-12_09:00:31.656 DEBUG e.u.box.client.BoxServiceClient - Name : Expires, Value: Thu, 19 Nov 1981 08:52:00 GMT
2015-01-12_09:00:31.656 DEBUG e.u.box.client.BoxServiceClient - Name : Cache-Control, Value: no-store, no-cache, must-revalidate, post-check=0, pre-check=0
2015-01-12_09:00:31.656 DEBUG e.u.box.client.BoxServiceClient - Name : Pragma, Value: no-cache
2015-01-12_09:00:31.656 DEBUG e.u.box.client.BoxServiceClient - Name : Set-Cookie, Value: presentation=desktop; path=/; domain=.box.com; secure
2015-01-12_09:00:31.656 DEBUG e.u.box.client.BoxServiceClient - Name : Set-Cookie, Value: box_redirect_url=value; expires=Thu, 01-Jan-1970 00:00:01 GMT; path=/; domain=.box.com
2015-01-12_09:00:31.657 DEBUG e.u.box.client.BoxServiceClient - Name : Set-Cookie, Value: box_redirect_rm=value; expires=Thu, 01-Jan-1970 00:00:01 GMT; path=/; domain=.box.com
2015-01-12_09:00:31.657 DEBUG e.u.box.client.BoxServiceClient - Name : Set-Cookie, Value: box_referrer_url=value; expires=Thu, 01-Jan-1970 00:00:01 GMT; path=/; domain=.box.com
2015-01-12_09:00:31.657 DEBUG e.u.box.client.BoxServiceClient - Name : Age, Value: 1
2015-01-12_09:00:31.657 DEBUG e.u.box.client.BoxServiceClient - Name : Transfer-Encoding, Value: chunked
2015-01-12_09:00:31.657 DEBUG e.u.box.client.BoxServiceClient - Name : Connection, Value: keep-alive
2015-01-12_09:00:31.657 DEBUG e.u.box.client.BoxServiceClient - Status: HTTP/1.1 200 OK
2015-01-12_09:00:31.737 DEBUG e.u.box.client.BoxServiceClient - Response content: <!DOCTYPE html><html lang="en-US"><head><title></title><meta name="viewport" content="initial-scale = 1.0,maximum-scale = 1.0" /><link rel="stylesheet" href="https://e1.boxcdn.net/_assets/css/section_templ_webviews_login_oauth_consent-0q8EWY.css"/><link rel="stylesheet" href="https://e1.boxcdn.net/_assets/css/webviews/webviews_image_ios-_ORMpz.css"/><script type="text/javascript">var request_token = '0d27ae4...
2015-01-12_09:00:31.738 DEBUG e.u.box.client.BoxServiceClient - 4319 bytes were written to file, ic_token.txt
2015-01-12_09:00:31.738 DEBUG e.u.box.client.BoxServiceClient - ic_token has been cached (...9fdc6)
2015-01-12_09:00:31.738 DEBUG edu.ucmerced.box.client.JoyRide - ic_token: ...9fdc6

2015-01-12_09:00:31.739 DEBUG e.u.box.client.BoxServiceClient - Created query: ?response_type=code&client_id=d76f7ks03abue3jg72k0zt1d82w14y35
2015-01-12_09:00:32.120 DEBUG e.u.box.client.BoxServiceClient - Request Method: POST
2015-01-12_09:00:32.121 DEBUG e.u.box.client.BoxServiceClient - Name : Server, Value: ATS
2015-01-12_09:00:32.121 DEBUG e.u.box.client.BoxServiceClient - Name : Date, Value: Mon, 12 Jan 2015 16:59:16 GMT
2015-01-12_09:00:32.121 DEBUG e.u.box.client.BoxServiceClient - Name : Content-Type, Value: text/html; charset=UTF-8
2015-01-12_09:00:32.121 DEBUG e.u.box.client.BoxServiceClient - Name : Content-Length, Value: 0
2015-01-12_09:00:32.121 DEBUG e.u.box.client.BoxServiceClient - Name : Expires, Value: Thu, 19 Nov 1981 08:52:00 GMT
2015-01-12_09:00:32.121 DEBUG e.u.box.client.BoxServiceClient - Name : Cache-Control, Value: no-store, no-cache, must-revalidate, post-check=0, pre-check=0
2015-01-12_09:00:32.121 DEBUG e.u.box.client.BoxServiceClient - Name : Pragma, Value: no-cache
2015-01-12_09:00:32.122 DEBUG e.u.box.client.BoxServiceClient - Name : Set-Cookie, Value: presentation=desktop; path=/; domain=.box.com; secure
2015-01-12_09:00:32.122 DEBUG e.u.box.client.BoxServiceClient - Name : Location, Value: https://www.google.com?state=&code=RXGo8445wInAYXPuEmCvLMVVO9hBQWym
2015-01-12_09:00:32.122 DEBUG e.u.box.client.BoxServiceClient - Name : Age, Value: 0
2015-01-12_09:00:32.122 DEBUG e.u.box.client.BoxServiceClient - Name : Connection, Value: keep-alive
2015-01-12_09:00:32.122 DEBUG e.u.box.client.BoxServiceClient - Status: HTTP/1.1 302 Found
2015-01-12_09:00:32.122 DEBUG e.u.box.client.BoxServiceClient - Response content: 
2015-01-12_09:00:32.123 DEBUG e.u.box.client.BoxServiceClient - 0 bytes were written to file, auth_token.txt
2015-01-12_09:00:32.123 DEBUG e.u.box.client.BoxServiceClient - auth_token has been cached (...BQWym)
2015-01-12_09:00:32.123 DEBUG edu.ucmerced.box.client.JoyRide - auth_token: ...9fdc6

2015-01-12_09:00:33.163 DEBUG e.u.box.client.BoxServiceClient - Request Method: POST
2015-01-12_09:00:33.163 DEBUG e.u.box.client.BoxServiceClient - Name : Server, Value: nginx
2015-01-12_09:00:33.163 DEBUG e.u.box.client.BoxServiceClient - Name : Date, Value: Mon, 12 Jan 2015 16:59:17 GMT
2015-01-12_09:00:33.164 DEBUG e.u.box.client.BoxServiceClient - Name : Content-Type, Value: application/json
2015-01-12_09:00:33.164 DEBUG e.u.box.client.BoxServiceClient - Name : Transfer-Encoding, Value: chunked
2015-01-12_09:00:33.164 DEBUG e.u.box.client.BoxServiceClient - Name : Connection, Value: keep-alive
2015-01-12_09:00:33.164 DEBUG e.u.box.client.BoxServiceClient - Name : Set-Cookie, Value: presentation=desktop; path=/; domain=.box.com; secure
2015-01-12_09:00:33.164 DEBUG e.u.box.client.BoxServiceClient - Name : Cache-Control, Value: no-store
2015-01-12_09:00:33.164 DEBUG e.u.box.client.BoxServiceClient - Status: HTTP/1.1 200 OK
2015-01-12_09:00:33.164 DEBUG e.u.box.client.BoxServiceClient - Response content: {"access_token":"53JQ7L0HWXvZroIBVbrf1CkmZgRMJb4N","expires_in":4138,"restricted_to":[],"refresh_token":"OwiacjDOPyZQLVd5Xs6HBYBbyNh7C4FRhzu1C3sXxmnKh6WtrhW07bfs54DCrLKq","token_type":"bearer"}
2015-01-12_09:00:33.165 DEBUG e.u.box.client.BoxServiceClient - 193 bytes were written to file, access_token.txt
2015-01-12_09:00:33.196 DEBUG e.u.box.client.BoxServiceClient - access_token has been cached (...MJb4N)
2015-01-12_09:00:33.196 DEBUG edu.ucmerced.box.client.JoyRide - access_token: ...MJb4N

2015-01-12_09:00:33.694 DEBUG e.u.box.client.BoxServiceClient - Request Method: GET
2015-01-12_09:00:33.695 DEBUG e.u.box.client.BoxServiceClient - Name : Server, Value: nginx
2015-01-12_09:00:33.695 DEBUG e.u.box.client.BoxServiceClient - Name : Date, Value: Mon, 12 Jan 2015 16:59:18 GMT

2015-01-12_09:00:33.695 DEBUG e.u.box.client.BoxServiceClient - Name : Content-Type, Value: application/json
2015-01-12_09:00:33.695 DEBUG e.u.box.client.BoxServiceClient - Name : Content-Length, Value: 423
2015-01-12_09:00:33.695 DEBUG e.u.box.client.BoxServiceClient - Name : Connection, Value: keep-alive
2015-01-12_09:00:33.696 DEBUG e.u.box.client.BoxServiceClient - Name : Cache-Control, Value: no-cache, no-store
2015-01-12_09:00:33.696 DEBUG e.u.box.client.BoxServiceClient - Status: HTTP/1.1 200 OK
2015-01-12_09:00:33.696 DEBUG e.u.box.client.BoxServiceClient - Response content: {"type":"user","id":"999999999","name":"David V","login":"davidv@yahoo.com","created_at":"2014-10-23T10:35:42-07:00","modified_at":"2015-01-12T08:59:16-08:00","language":"en","timezone":"America\/Chicago","space_amount":10737418240,"space_used":466445,"max_upload_size":2147483648,"status":"active","job_title":"","phone":"2092590013","address":"","avatar_url":"https:\/\/app.box.com\/api\/avatar\/large\/226256685"}
2015-01-12_09:00:33.696 DEBUG e.u.box.client.BoxServiceClient - 423 bytes were written to file, my_box_profile.txt
2015-01-12_09:00:33.697 DEBUG edu.ucmerced.box.client.JoyRide - my_box_profile: ...685"}
```

