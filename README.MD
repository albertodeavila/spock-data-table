# Spock Data table improvements
This Jetbrains plugin allows you to improve the use of [Spock](http://spockframework.org/) [data table](http://spockframework.org/spock/docs/1.0/data_driven_testing.html).

Spock is a testing framework and one of its features is to create a [data table](http://spockframework.org/spock/docs/1.0/data_driven_testing.html) that looks like: 
```
class Math extends Specification {
    def "maximum of two numbers"(int a, int b, int c) {
        expect:
        Math.max(a, b) == c

        where:
        a | b | c
        1 | 3 | 3
        7 | 4 | 7
        0 | 0 | 0
    }
}

```
With this kind of data structure, you can execute the expectation block, `Math.max(a, b) == c`, for every line specified in the data table.

## Instalation
You can find this plugin available on [Jetbrains plugins page](https://plugins.jetbrains.com/plugin/14351-spock-data-table-improvements) or directly throught your IDE, under Plugins section, searching by _Spock data table_




## Features
What can you do with this plugin? 
* [Add a new column with multiple values, creating more lines if needed](#add-a-new-column)
* [Remove an existing column](#remove-an-existing-column)
* [Create an entire data table, generating all existing cases](#create-an-entire-data-table)
* [Edit an specific value in a column](#edit-an-specific-value-in-a-column)
* [Show the case number in all data tables](#show-the-case-number)
* [Hide case number in all data tables](#hide-case-number-in-all-data-tables)

***
###Add a new column

If you have a test such as: 
``` 
def "maximum of two numbers"(int a, int b, int c) {
    expect:
    Math.max(a, b) == c

    where:
    a | b | c
    1 | 3 | 3
    7 | 4 | 7
    0 | 0 | 0
}
```

and you want to add a new column, following the sintax `varName => [val1#val2]`, you can write: 
``` 
def "maximum of two numbers"(int a, int b, int c) {
    expect:
    Math.max(a, b) == c

    where:
    a | b | c | d => [2]
    1 | 3 | 3
    7 | 4 | 7
    0 | 0 | 0
}
```
When you select the `d => [2]` and then press **Tools** > **Spock data table** > **Add selected column**, then the table appears as: 
``` 
def "maximum of two numbers"(int a, int b, int c) {
    expect:
    Math.max(a, b) == c

    where:
    a | b | c | d 
    1 | 3 | 3 | 2
    7 | 4 | 7 | 2
    0 | 0 | 0 | 2
}
```

If you want to add more than one value, for example, `d => [2#5]`, it will generate: 
``` 
def "maximum of two numbers"(int a, int b, int c) {
    expect:
    Math.max(a, b) == c

    where:
    a | b | c | d 
    1 | 3 | 3 | 2
    7 | 4 | 7 | 2
    0 | 0 | 0 | 2

    1 | 3 | 3 | 5
    7 | 4 | 4 | 5
    0 | 0 | 0 | 5
}
```

###Remove an existing column
You can remove a selected column, for example, if we want to remove `b` column from the following example: 
``` 
def "maximum of two numbers"(int a, int b, int c) {
    expect:
    Math.max(a, b) == c

    where:
    a | b | c 
    1 | 3 | 3
    7 | 4 | 7
    0 | 0 | 0
}
```
You just need to select the column name and press **Tools** > **Spock data table** > **Add selected column**, and it will generate: 
``` 
def "maximum of two numbers"(int a, int b, int c) {
    expect:
    Math.max(a, b) == c

    where:
    a | c 
    1 | 3
    7 | 4
    0 | 0
}
```

###Create an entire data table 
Following the *add new column* sintax, if we want to create a entire table, we just need to write:

`fruits => [Fruit.BANANA, Fruit.APPLE] | prices [2.34#4.45] | currency ['€', '$']` 

With the cursor on that line, if we press on **Tools** > **Spock data table** > **Generate data table**, it will appear something like: 

```
fruits          | prices | currency 
FRUIT.BANANA    | 2.34   | '€'
FRUIT.BANANA    | 2.34   | '$'
FRUIT.BANANA    | 4.45   | '€'
FRUIT.BANANA    | 4.45   | '$'

FRUIT.APPLE     | 2.34   | '€'
FRUIT.APPLE     | 2.34   | '$'
FRUIT.APPLE     | 4.45   | '€'
FRUIT.APPLE     | 4.45   | '$'
```


###Edit an specific value in a column

###Show the case number
When you have tables with a bunch of options and, sometimes, some cases fail, you will have that failing cases are, for example, 13 and 17. 
With this feature you can see at a quick glance what's the case number.
Suppose that we have the following test: 

```
void "My sum"() {
    expect:
    a + b == c

    where:
    a  | b | c
    0  | 0 | 0
    1  | 1 | 2
    0  | 1 | 1
    1  | 0 | 1
    2  | 2 | 4
    4  | 5 | 9

    0  | 0 | 0
    -1 | 1 | 0
    0  | 1 | 1
    -1 | 0 | -1
    -2 | 2 | 0
    -4 | 5 | 1

    0  | 0  | 0
    1  | -1 | 0
    0  | -1 | -1
    1  | 0  | 1
    2  | -2 | 0
    4  | -5 | -1
}
```

When you press on **Tools** > **Spock data table** > **Write # case**, the test will be like:
```
void "My sum"() {
    expect:
    a + b == c

    where:
    /*___#___*/a  | b | c
    /*___0___*/0  | 0 | 0
    /*___1___*/1  | 1 | 2
    /*___2___*/0  | 1 | 1
    /*___3___*/1  | 0 | 1
    /*___4___*/2  | 2 | 4
    /*___5___*/4  | 5 | 9

    /*___6___*/0  | 0 | 0
    /*___7___*/-1 | 1 | 0
    /*___8___*/0  | 1 | 1
    /*___9___*/-1 | 0 | -1
    /*___10__*/-2 | 2 | 0
    /*___11__*/-4 | 5 | 1

    /*___12__*/0  | 0  | 0
    /*___13__*/1  | -1 | 0
    /*___14__*/0  | -1 | -1
    /*___15__*/1  | 0  | 1
    /*___16__*/2  | -2 | 0
    /*___17__*/4  | -5 | -1
}
```


###Hide case number in all data tables 
This action just hides the [show case number](#show-the-case-number)