# chess
Chess engine implementation and consumable libraries for chess engine development in Java.

### perft
##### (Benchmark: qperft - ~190M nodes/sec, single-thread, hashing disabled)
- [2019-09-08] 1.962M nodes/sec (First "fully working" iteration of move generator)
- [2019-09-08] 2.676M nodes/sec (Made `bitmask` a public final property in enums, instead of encapsulated property)
- [2019-09-09] 2.429M nodes/sec (Refactored enums in chess-core to all be public properties of Square)
- [2019-09-10] 3.188M nodes/sec (Replaced `java.util.HashSet` with `it.unimi.dsi.fastutil.objects.ObjectOpenHashSet` in BitboardUtils and MagicBitboardMoveGenerator apinned piece logic)
- [2019-09-10] 3.240M nodes/sec (Replaced `java.util.LinkedList` with `it.unimi.dsi.fastutil.objects.ObjectOpenArrayList` in MagicBitboardMoveGenerator)
- [2019-09-10] 3.821M nodes/sec (Refactored Board to store Map of Squares and Pieces and mutate instead of creating from scratch on demand)
- [2019-09-10] 3.786M nodes/sec (Reverted public properties change back to encapsulated fields)
