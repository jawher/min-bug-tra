Min-Bug-Tra
=======================

A minimal bug tracker developed with Wicket that serves as an example application for this [tutorial](http://jawher.wordpress.com/i-wrote/creation-application-crud-wicket/)

Building
--------

You need a Java 5 (or newer) environment and Maven 3 installed:

    $ mvn --version
    Apache Maven 2.2.1 (r801777; 2009-08-06 20:16:01+0100)
    Java version: 1.6.0_22
    Java home: /usr/lib/jvm/java-6-sun-1.6.0.22/jre
    Default locale: en_US, platform encoding: UTF-8
    OS name: "linux" version: "2.6.35-25-generic" arch: "i386" Family: "unix"

You should now be able to launch `min-bug-tra` which will be available at this address http://localhost:8080 :

    $ git clone git://github.com/jawher/min-bug-tra.git
    $ cd min-bug-tra
    $ mvn clean jetty:run


Troubleshooting
---------------

Please consider using [Github issues tracker](http://github.com/jawher/min-bug-tra/issues) to submit bug reports or feature requests.


License
-------

See `LICENSE` for details.
