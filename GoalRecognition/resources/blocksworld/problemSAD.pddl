(define (problem blocks_words)
	(:domain blocksworld)
(:objects 
S R D B E A - block
)
(:init
(on-table S)
(on-table R)
(on-table B)
(on-table A)
(on D B)
(on E A)
(clear S)
(clear R)
(clear D)
(clear E)
(arm-empty)
)
(:goal (and
(on A D) (on S A) (on-table D) (clear S)
))
)
