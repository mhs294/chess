# chess
Chess engine implementation and consumable libraries for chess engine development in Java.

### perft
##### (Benchmark: qperft - 190M nodes/sec, single-thread, hashing disabled)
- [2019-09-08] 1.962M nodes/sec (First "fully working" iteration of move generator)
- [2019-09-08] 2.676M nodes/sec (Made `bitmask` a public final property in enums, instead of encapsulated property)
- [2019-09-09] 2.429M nodes/sec (Refactored enums in chess-core to all be public properties of Square)
