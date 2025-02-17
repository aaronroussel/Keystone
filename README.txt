Requirements:
Java v22.0.2 (Azul Zulu SDK)
GDAL 3.9.0 or newer



Concerning GDAL:

GDAL's java bindings MUST be built from source using cmake. There are a number of other dependencies you will need to
be able to successfully build gdal and the bindings.

The minimum requirements to build GDAL are:

    CMake >= 3.16, and an associated build system (make, ninja, Visual Studio, etc.)

    C99 compiler

    C++17 compiler since GDAL 3.9 (C++11 in previous versions)

    PROJ >= 6.3.1

Additional requirements to run the GDAL test suite are:

    SWIG >= 4, for building bindings to other programming languages

    Python >= 3.8

    Python packages listed in autotest/requirements.txt

more info on building from source can be found here:
https://gdal.org/en/stable/development/building_from_source.html
https://gdal.org/en/stable/api/java/index.html

for the first cmake command we want to specify that we would like to build the java bindings. this is set to "ON" by default but if we explicity
specify this option then cmake should tell us if we are missing any dependencies and abort if that is the case. Otherwise it would continue with
the build if we are missing dependencies for the java bindings, and just build without java bindings.

ex: "cmake .. -DBUILD_JAVA_BINDINGS=ON"             ------>   the leading "D" here is important. forgetting to begin build configs with this will cause issues



I would advise to pipe your output from the final stage of the build process into a text file. The output will display
the installation locations for all of GDAL. These locations will be useful when setting up PATH environment variables
on your system. You can do this by using ">" after the cmake command and enter a file name if the file is in the same directory or the full path
to the file if it is in another directory.

ex: 'cmake --build . --target install > output.txt

this will not show any output in the terminal, if you want to also see the output in the console look into how the "tee" terminal command works.
you can also just choose to leave your terminal window open until you get things working properly.



You will need to add the path to the wherever your libgdal.so (libgdal.dll on windows) folder, which contains library files for gdal, to your system
LD_LIBRARY_PATH (PATH on windows). There are both temporary and permanent ways to do this, and we want the permanent
option. How to do this will vary depending on operating system.

Then add the following as VM arguments in your build options in intellij:
 -Djava.library.path={path to jni folder}

If this does not work, you may also need to add this to the build configuration. open the build config and in the box labeled: "Environment Variables" add:

PATH={path to libgdal.dll):$PATH






