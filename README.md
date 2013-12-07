# Hammock [![Build Status](https://travis-ci.org/whiskeysierra/hammock.png?branch=master)](http://travis-ci.org/whiskeysierra/hammock)

![Hammock](http://farm1.staticflickr.com/89/255289520_04e0b04c7e_m_d.jpg)

A high-order function generator for the Java Platform. 

## Motivation
Everyone who is trying to use functional language features in Java usually runs 
across Guava's [Predicate](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/base/Predicate.html) 
and [Function](http://docs.guava-libraries.googlecode.com/git/javadoc/com/google/common/base/Function.html) interfaces.
They allow you to write very nice, functional style code but usually require
anonymous classes:

```java
final Iterable<String> names = FluentIterable.from(members).transform(new Function<Member, String>() {

    @Nullable
    @Override
    public String apply(@Nullable(Member member) {
        return member.getName();
    }

});
```

You can now extract the anonymous class to a local/instance variable and into 
an inner class and use static imports to shorten that:

```java
final Iterable<String> names = FluentIterable.from(members).transform(getName());
```

Pretty compact, but you'll still need to write that function yourself which is
quite a lot of boilerplate code. This is where Hammock comes into play:

## Maven

Hammock comes in two parts: an API and an annotation processor. You'll need both
dependencies just during compilation. The only runtime dependency is Guava.

```xml
<dependency>
    <groupId>com.codereligion</groupId>
    <artifactId>hammock-api</artifactId>
    <version>${hammock.version}</version>
    <!-- use scope compile if you don't have Guava on your classpath -->
    <scope>provided</scope>
</dependency>
<dependency>
    <groupId>com.codereligion</groupId>
    <artifactId>hammock-compiler</artifactId>
    <version>${hammock.version}</version>
    <scope>provided</scope>
</dependency>

```

## Quickstart

The first step is to annotate the methods you want to use:
```java
import com.codereligion.hammock.Functor;

public final class Member {

    ...
    
    @Functor
    public String getName() {
        return name;
    }

}
```

That's it! As long as you have the hammock compiler on your classpath during
compilation it will generate java source files for you which contain re-usable
stateless singleton implementations for your functions. For every class with
at least one annotated method Hammock will create a corresponding class in the
same package. In our example there will be a `Member_` class.

Again assuming you're using static imports you can now use it like this:
```java
FluentIterable.from(members).transform(getName()).copyInto(names);
```

The generated methods will always have the same name as the getter/accessor they
delegate to, so there should be little question what it actually does. But even
if there is, since the generated source files are in your project, you can just
browse them if you want.

## Advanced usages

### Renaming
In case you want to change the name of the generated method so something more readable,
just use `@Functor(name = "toName")` which will result in a usage like this:

```java
FluentIterable.from(members).transform(toName()).copyInto(names);
```

### Graceful null handling
By default parameters are not inspected in any way and just used/passed to the underlying
method. If you want to explicitly handle nulls gracefully, use `@Functor(graceful = true)`.

For functions the method will check for nulls and return null while predicates
default to false for null inputs.

In case you want to treat null inputs to predicates as true use `@Functor(nullTo = true)`.

### Parameters
The most common use case are probably property accessors which usually don't have
any parameters, which can optimized by returning static singleton instances
for requested functions and predicates.
 
In case there are parameters on the method, the returned functor is no longer
a static singleton, it is still an immutable instance though.
 
Given the following example:

```java 
public class Member {
 
    private int age;
    
    @Functor
    public boolean isOlderThan(int minimumAge) {
       return age > minimumAge;
    }
    
}
```
 
Can be used like this:

```java
FluentIterable.from(members).filter(isOlderThan(30)).copyInto(seniors);
```

### Statics
For instance methods the input type for the resulting functor is always the enclosing type.
When annotating static methods this behaviour is different. Static methods require at least
on parameter, which is than considered the input to the function. In case you have multiple
parameters you need to annotate the parameter which acts as an input with `@Input`:

```java
public class Strings {

    @Functor
    public static String toLowerCase(@Input String s, Locale locale) {
        return s.toLowerCase(locale);
    }

}
```

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
