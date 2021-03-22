
(define (problem blocks_words)
	(:domain blocks)
(:objects 

D R A W O E P C - block
)
(:init
(handempty)
(clear O)
(ontable O)
(clear R)
(on R P)
(ontable P)
(clear E)
(ontable E)
(clear D)
(on D A)
(on A C)
(ontable C)
(clear W)
(ontable W)
)
(:goal (and
(CLEAR D) (ONTABLE W) (ON D R) (ON R A) (ON A W)
))
)
