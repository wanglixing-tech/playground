package crs.fcl.integration.iib

class GroovyStringBasic {
    static main(args) {
        def name = 'John' // John
        println name
        println 'The quick brown fox jumps over the lazy dog'.length() // 43
        println 'Non-Blocking IO'.indexOf("o") // 1
        println 'AngularJS Service vs Factory vs Provider'.substring(32) // -Provider
        println 'C# is the best'.replace('C#', 'Java') // Java is the best
        println 'I am very angry'.toUpperCase() // I AM VERY ANGRY
        }
}

