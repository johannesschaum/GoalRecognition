(define (problem blocks_words)
	(:domain blocksworld)
(:objects 
A B C D - block
)
(:init
(on-table A)
(on-table B)
(on-table C)
(on D C)
(clear A)
(clear B)
(clear D)
(arm-empty)
)
(:goal (and
(on C A) (on B D)
))
)
