# Orma for Android [![Circle CI](https://circleci.com/gh/gfx/Android-Orma/tree/master.svg?style=svg)](https://circleci.com/gh/gfx/Android-Orma/tree/master) [ ![Download](https://api.bintray.com/packages/gfx/maven/orma/images/download.svg) ](https://bintray.com/gfx/maven/orma/_latestVersion)

* Note that this is an **alpha** software and the interface will change until v1.0.0.

Orma is a lightning-fast ORM for Android, generating helper classes at compile time with annotation processing.

There are already [a lot of ORMs](https://android-arsenal.com/tag/69). Why I have to add another?

The answer is that I need ORM that have the following features:

* Fast
* Model classes must have no restriction
  * They might be POJO, Parcelable and/or even models that are managed by another ORM
  * They should be passed to another thread
* Database handles must be instances
  * Not a singleton nor static-method based
* Automatic migration
  * For what can be detected logically
  * i.e. simple `add column` and `drop column`

They are just what Orma has. This is as fast as Realm, its models have no restriction, database handle is
not a singleton, and has `SchemaDiffMigration`, which detects `add column` and `drop column` automatically.

# Install

```groovy
dependencies {
    apt 'com.github.gfx.android.orma:orma-processor:0.5.0'
    provided 'com.github.gfx.android.orma:orma-annotations:0.5.0'
    compile 'com.github.gfx.android.orma:orma:0.5.0'
}
```

# Synopsis

First, define model classes annotated with `@Table`, `@Column`, and `@PrimaryKey`.

```java
package com.github.gfx.android.orma.example;

import com.github.gfx.android.orma.annotation.Column;
import com.github.gfx.android.orma.annotation.PrimaryKey;
import com.github.gfx.android.orma.annotation.Table;

import android.support.annotation.Nullable;

@Table
public class Todo {

    @PrimaryKey
    public long id;

    @Column(indexed = true)
    public String title;

    @Column
    @Nullable
    public String content;

    @Column
    public long createdTimeMillis;
}
```

Second, create a database handle `OrmaDatabase`, which is generated by `orma-processor`.

```java
OrmaDatabase orma = new OrmaDatabase(context, "orma.db");
orma.addTypeAdapters(TypeAdapterRegistry.defaultTypeAdapters());
```

Then, you can create, read, update and delete models.

```java
Todo todo = ...;

// create
orma.insertIntoTodo(todo);

// prepared statements with transaction
orma.transaction( -> {
    Inserter<Todo> inserter = orma.prepareInsertIntoTodo();
    inserter.execute(todo);
});

// read
orma.selectFromTodo()
  .where("title = ?", "foo")
  .toList();

// update
orma.updateTodo()
  .where("title = ?", "foo")
  .content("a new content")
  .execute();

// delete
orma.deleteTodo()
  .where("title = ?", "foo")
  .execute();
```

(this document is working in progress.)

# Models

## Accessors

You can define private columns with `@Getter` and `@Setter`, which tells `orma-processor` to use accessors.

```
@Table
public class KeyValuePair {

    @Column
    private String key;

    @Column
    private String value;

    @Getter("key")
    public String getKey() {
        return key;
    }

    @Setter("key")
    public void setKey(String key) {
        this.key = key;
    }

    @Getter("value")
    public String getValue() {
        return value;
    }

    @Setter("value")
    public void setValue(String value) {
        this.value = value;
    }
}
```

# Migration

The default migration engine, `SchemaDiffMigration`, can handle column additions and removals.

You can also set a custom migration engine:

```java
class CustomMigrationEngine implements MigrationEngine { ... }

OrmaDatabase orma = new OrmaDatabase(context, "orma.db", new CustomMigrationEngine());
```

See [migration/](migration/) for details.

(TODO: more concise description)

# Type Adapters

Type adapters, which serializes and deserializes custom classes, are supported.

If you use type adapters, you can add them to `OrmaDatabase`:

```java
class FooAdapter extends AbstractTypeAdapter<Foo> {
    @Override
    @NonNull
    public String serialize(@NonNull Foo source) {
        return ... serialize ...;
    }

    @Override
    @NonNull
    public Foo deserialize(@NonNull String serialized) {
        return ... deserialize ...;
    }
}

OrmaDatabase orma = ...;
orma.addTypeAdapters(TypeAdapterRegistry.defaultTypeAdapters()); // add built-ins
orma.addTypeAdapters(new FooAdapter());
```

## Default Type Adapters

There are a few built-in type adapter provided by default:

* `StringListAdapter` for `List<String>`
* `StringSetAdapter` for `Set<String>`
* `DateAdapter` for `Date`
* `UriAdapter` for `Uri`

Currently, you should explicitly register with
`orma.addTypeAdapters(TypeAdapterRegistry.defaultTypeAdapters()`.

# Example

There is an example app to show how to use Orma.

See [example/](example/) for details

# Support

* Use [GitHub issues](https://github.com/gfx/Android-Orma/issues) for the issue tracker
* Feel free to ask for questions to the author [@_\_\gfx_\_\](https://twitter.com/__gfx__)

# Licenses in Runtime Dependencies

* https://github.com/ReactiveX/RxJava - Apache Software License 2.0
* https://github.com/JSQLParser/JSqlParser - LGPL v2.1 and Apache Software License 2.0 (dual licenses)

# Release Engineering

```shell
./gradlew bumpMajor # or bumpMinor / bumpPatch
make publish # does release engineering
```

# Author

FUJI Goro (gfx).

# License

The MIT License.

Copyright (c) 2015 FUJI Goro (gfx) <gfuji@cpan.org>.

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
