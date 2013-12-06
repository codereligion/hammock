# Hammock [![Build Status](https://travis-ci.org/whiskeysierra/hammock.png?branch=master)](http://travis-ci.org/whiskeysierra/hammock)

![Hammock](http://farm1.staticflickr.com/89/255289520_04e0b04c7e_m_d.jpg)

A high-order function meta-model generator for the Java Platform. 

## Motivation
Everyone who is trying to use functional language features in Java usually runs 
across Guava's [Predicate](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/base/Predicate.html) 
and [Function](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/base/Function.html) interfaces.
They allow you to write very nice, functional style code but usually require
anonymous classes:

```java
final Iterable<String> names = Iterables.transform(stooges, new Function<Member, String>() {

    @Nullable
    @Override
    public String apply(@Nullable(Member member) {
        return member == null ? null : member.getName();
    }

});
```

You can now extract the anonymous class to a local/instance variable and into 
an inner class and use static imports to shorten that:

```java
final Iterable<String> names = transform(stooges, getName);
```

Pretty compact but you'll still need to write that function yourself which is
quite a lot of boilerplate code. Since in 90% of the cases your functions just
delegate to getters or other bean accessors this could easily be automated.
This is where Hammock comes into play:

## Quickstart

Hammock comes in two parts: an API and an annotation processor. You'll need both
dependencies just during compilation, i.e. in the *provided* scope if you are
using maven. The only runtime dependency is Guava.

The first step is to annotate the methods you want to use:
```java
import com.codereligion.hammock.FirstClass;

public final class Member {

    ...
    
    @FirstClass
    public String getName() {
        return this.name;
    }

}
```

That't it! As long as you have the hammock compiler on your classpath during
compilation it will generate java source files for you which contain re-usable
stateless singleton implementations for your functions. For every class with
at least one annotated method Hammock will create a corresponding class in the
same package. In our example there will be a `Member_` class.

Again assuming you're using static imports you can now use it like this:
```java
final Iterable<String> names = transform(stooges, getName());
```

The generated methods will always have the same name as the getter/accessor they
delegate to, so there should be little question what it actually does. But even
if there is, since the generated source files are in your project, you can just
browse them if you want.

## Attributions
![Creative Commons License](http://i.creativecommons.org/l/by-nc-nd/2.0/80x15.png)
Hammock photo by [Sean Martell](http://www.flickr.com/photos/mart3ll/) is licensed under a
[Attribution-NonCommercial-NoDerivs 2.0 Generic](http://creativecommons.org/licenses/by-nc-nd/2.0/).

## Boring legal stuff
The MIT License (MIT)

Copyright (c) 2013 Willi Schönborn

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
