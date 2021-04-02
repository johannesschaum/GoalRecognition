(define (problem blocks_words)
	(:domain blocksworld)
(:objects 
A B - block
)
(:init
(on-table A)
(on B A)
(clear B)
(arm-empty)
)
(:goal (and
(on A B)
))
)
