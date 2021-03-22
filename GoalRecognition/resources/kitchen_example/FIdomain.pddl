(define (domain combined)
	(:requirements :adl)
	(:types
		openobjects nonopenobjects - objects
        part
        walkl
        objects - object
	)
    (:constants
        cupboard_bl garbage cupboard_br cupboard_tl cupboard_tm cupboard_tr drawer oven fridge jam_glass jam_glass_2 peanut_butter_glass bread_bag egg_box egg_shella egg_shellb pepper_shaker salt_shaker butter_spray_can oil_bottle brownie_box brownie_bag tap pizza_sauce_can pizza_dough_container cheese_wrapper salad_wrapper crouton_wrapper celery_wrapper cucumber_wrapper carrot_wrapper salami_wrapper salad_sauce_bottle - openobjects
        knife knifeb spatula whisk spoon hands fork peeler counter fridge_counter stove plate bowl bowl_s measuring_cup_s measuring_cup_l pan baking_pan cheese_grater paper_towela paper_towelb towel cutting_board board jam peanut_butter peanut_butter_glass_cap jam_glass_cap breada breadb clip eggs egga eggb water sink oil_bottle_cap oil butter butter_spray_cap brownie_mix dough salt pepper pizza_dough pizza_sauce pizza_sauce_can_cap salami salami_piecea salami_pieceb salami_piecec salami_pieced cheese cheese_piece salad salad_sauce celery celery_piecea celery_pieceb celery_piecec celery_pieced cucumber cucumber_piecea cucumber_pieceb cucumber_piecec cucumber_pieced carrot carrot_piecea carrot_pieceb carrot_piecec carrot_pieced crouton - nonopenobjects
        partially - part
        fridge_place counter_place - walkl

    )
    (:predicates
		(is-open ?what - objects)
		(partially-is-open ?what - objects)
		(att ?where - walkl)
        (is-at ?what - objects ?fromto - objects)
        (is-on ?what - objects)
        (in ?what - objects ?to - objects)
        (partially-in ?what - objects ?to - objects)
        (is-stirred ?what - objects)
        (is-partially-stirred ?what - objects)
        (is-clean ?what - objects)
        (cooked ?what - objects)
        (shaken ?what - objects)
        (is-peeled ?what - objects)
        (is-unrolled ?what - objects)
        (is-rinsed ?what - objects)
        (take-pair ?what - objects ?fromto - objects)
		(fill-triple ?what - objects ?from - objects ?to - objects)
		(turn-single ?what - objects)
		(stir-pair ?where ?with)
		(clean-pair ?what ?with)
		(cook-triple ?what ?in ?where)
		(shake-single ?what)
		(cut-pair ?what ?with)
		(peel-pair ?what ?with)
		(unroll-single ?what)
		(rinse-single ?what)
        (made_eggs)
        (made_pizza)
        (made_brownie)
        (made_sandwich)
        (made_salad)
    )
	(:functions
		(total-cost)
	)
    (:action open
		:parameters (?what - openobjects)
		:precondition (and
							(imply (= ?what fridge)
							    (att fridge_place))
							(imply (not (= ?what fridge))
							    (att counter_place))
                            (not (is-open ?what))
                            (or
								(exists (?o - openobjects) (and (is-at ?what ?o) (is-open ?o)))
								(not (exists (?o - openobjects) (and (is-at ?what ?o))))
							)
						)
		:effect (and
		            (is-open ?what)
		            (when (= ?what oil_bottle)
						(and
							(is-at oil_bottle_cap hands)
                            (not (is-at oil_bottle_cap oil_bottle))
						)
					)
					(when (= ?what butter_spray_can)
						(and
							(is-at butter_spray_cap hands)
                            (not (is-at butter_spray_cap butter_spray_can))
						)
					)
					(when (and (= ?what bread_bag) (is-at clip bread_bag))
						(and
							(is-at clip hands)
							(not (is-at clip bread_bag))
						)
					)
					(when (= ?what peanut_butter_glass)
						(and
							(is-at peanut_butter_glass_cap hands)
							(not (is-at peanut_butter_glass_cap peanut_butter_glass))
						)
					)
					(when (= ?what jam_glass)
						(and
							(is-at jam_glass_cap hands)
							(not (is-at jam_glass_cap jam_glass))
						)
					)
					(when (= ?what pizza_sauce_can)
						(and
							(is-at pizza_sauce_can_cap hands)
							(not (is-at pizza_sauce_can_cap pizza_sauce_can))
						)
					)
					(increase (total-cost) 1)
		            )
	)
    (:action open
		:parameters (?what - objects ?how - part)
		:precondition (and
							(imply (= ?what fridge)
							    (att fridge_place))
							(imply (not (= ?what fridge))
							    (att counter_place))
                            (not (is-open ?what))
                            (or
								(exists (?o - openobjects) (and (is-at ?what ?o) (is-open ?o)))
								(not (exists (?o - openobjects) (and (is-at ?what ?o))))
							)
						)
		:effect (and
					(increase (total-cost) 1)
					(partially-is-open ?what)
		            )
	)
    (:action close
        :parameters (?what - openobjects)
        :precondition (and
                            (imply (= ?what fridge)
							    (att fridge_place))
							(imply (not (= ?what fridge))
							    (att counter_place))
                            (is-open ?what)
                            (or
								(exists (?o - openobjects) (and (is-at ?what ?o) (is-open ?o)))
								(not (exists (?o - openobjects) (and (is-at ?what ?o))))
							)
							(imply (= ?what jam_glass)
							    (is-at jam_glass_cap hands))
							(imply (= ?what peanut_butter_glass)
							    (is-at peanut_butter_glass_cap hands))
							(imply (= ?what pizza_sauce_can)
							    (is-at pizza_sauce_can_cap hands))
							(imply (= ?what butter_spray_can)
							    (is-at butter_spray_cap hands))
							(imply (= ?what oil_bottle)
							    (is-at oil_bottle_cap hands))
							(imply (= ?what bread_bag)
							    (is-at clip hands))
                        )
        :effect (and
                    (not (is-open ?what))
                    (when (= ?what oil_bottle)
						(and
							(not (is-at oil_bottle_cap hands))
                            (is-at oil_bottle_cap oil_bottle)
						)
					)
					(when (= ?what butter_spray_can)
						(and
							(not (is-at butter_spray_cap hands))
                            (is-at butter_spray_cap butter_spray_can)
						)
					)
					(when (and (= ?what bread_bag) (is-at clip bread_bag))
						(and
							(not (is-at clip hands))
							(is-at clip bread_bag)
						)
					)
					(when (= ?what peanut_butter_glass)
						(and
							(not (is-at peanut_butter_glass_cap hands))
							(is-at peanut_butter_glass_cap peanut_butter_glass)
						)
					)
					(when (= ?what jam_glass)
						(and
							(not (is-at jam_glass_cap hands))
							(is-at jam_glass_cap jam_glass)
						)
					)
					(when (= ?what pizza_sauce_can)
						(and
							(not (is-at pizza_sauce_can_cap hands))
							(is-at pizza_sauce_can_cap pizza_sauce_can)
						)
					)
					(increase (total-cost) 1)
                    )
    )
    (:action walk
		:parameters (?from ?to - walkl)
		:precondition (and
							(att ?from)
							(not (att ?to))
						)
		:effect (and
					(att ?to)
					(increase (total-cost) 1)
					(not (att ?from))
				)
	)
    (:action take
		:parameters (?what - objects ?from - objects)
		:precondition (and
							(is-at ?what ?from)
							(take-pair ?what ?from)
							(imply (= ?from fridge)
							    (att fridge_place))
							(imply (not (= ?from fridge))
							    (att counter_place))
							(imply (exists (?o - openobjects) (= ?from ?o))
									(is-open ?from)
								)
						)
		:effect (and
					(is-at ?what hands)
					(not (is-at ?what ?from))
					(increase (total-cost) 1)
				)
	)
    (:action put
		:parameters (?what - objects ?to - objects)
		:precondition (and
							(is-at ?what hands)
							(take-pair ?what ?to)
							(imply (= ?to fridge)
							    (att fridge_place))
							(imply (not (= ?to fridge))
							    (att counter_place))
							(imply (exists (?o - openobjects) (= ?to ?o))
									(is-open ?to)
								)
						)
		:effect (and
					(is-at ?what ?to)
					(not (is-at ?what hands))
					(increase (total-cost) 1)
				)
	)
    (:action turn_on
		:parameters (?what - objects)
		:precondition (and
							(not (is-on ?what))
							(att counter_place)
							(turn-single ?what)
						)
		:effect (and
		            (is-on ?what)
					(increase (total-cost) 1)
		            )
	)
    (:action turn_off
      		:parameters (?what - objects)
      		:precondition (and
      						(is-on ?what)
      						(att counter_place)
      						(turn-single ?what)
      					)
      		:effect (and
      		            (not (is-on ?what))
						(increase (total-cost) 1)
      		            )
    )

    (:action fill
        :parameters (?what - objects ?from - objects  ?to - objects)
        :precondition (and
                            (fill-triple ?what ?from ?to)
                            (imply (exists (?o - openobjects) (= ?from ?o))
									(is-open ?from)
								)
                            (in ?what ?from)
                            (imply (= ?what cheese)
                                (is-at cheese_grater hands))
                            (imply (= ?what peanut_butter)
                                (or (is-at knife hands) (is-at spoon hands)))
                            (imply (= ?what jam)
                                (or (is-at knife hands) (is-at spoon hands)))
                            (imply (= ?what pizza_sauce)
                                (or (is-at knife hands) (is-at spoon hands)))
                        )
        :effect (and
                    (in ?what ?to)
                    (not (in ?what ?from))
					(increase (total-cost) 1)
                )
    )
    (:action fill
        :parameters (?what - objects ?from - objects  ?to - objects ?how - part)
        :precondition (and
                            (fill-triple ?what ?from ?to)
                            (imply (exists (?o - openobjects) (= ?from ?o))
									(is-open ?from)
								)
                            (in ?what ?from)
                            (imply (= ?what cheese)
                                (is-at cheese_grater hands))
                            (imply (= ?what peanut_butter)
                                (or (is-at knife hands) (is-at spoon hands)))
                            (imply (= ?what jam)
                                (or (is-at knife hands) (is-at spoon hands)))
                            (imply (= ?what pizza_sauce)
                                (or (is-at knife hands) (is-at spoon hands)))
                        )
        :effect (and
                    (partially-in ?what ?to)
					(increase (total-cost) 1)
                )
    )
    (:action make-dough
        :parameters ()
        :precondition (and
                            (in egga bowl)
                            (in eggb bowl)
                            (in water bowl)
                            (in oil bowl)
                            (in butter bowl)
                            (in brownie_mix bowl)
                            (is-stirred bowl)
                        )
        :effect (and
                    (in dough bowl)
					(increase (total-cost) 1)
                )
    )
    (:action stir
		:parameters (?where - objects ?with - objects)
		:precondition (and
							(is-at ?with hands)
							(stir-pair ?where ?with)
							(or
								(is-at ?where hands)
								(is-at ?where counter)
							)
							(not (is-stirred ?where))
						)
		:effect (and
					(is-stirred ?where)
					(increase (total-cost) 1)
				)
	)
	(:action stir
		:parameters (?where - objects ?with - objects ?how - part)
		:precondition (and
                            (is-at ?with hands)
                            (stir-pair ?where ?with)
                            (or
                               (is-at ?where hands)
                               (is-at ?where counter)
                            )
                            (not (is-stirred ?where))
						)
		:effect (and
					(is-partially-stirred ?where)
					(increase (total-cost) 1)
				)
	)
    (:action clean
		:parameters (?what - objects ?with - objects)
		:precondition (and
							(clean-pair ?what ?with)
                            (is-at ?with hands)
                            (imply (not (= ?what hands))
                                (is-at ?what hands))
						)
		:effect (and
					(is-clean ?what)
					(increase (total-cost) 1)
					)
	)
	(:action have_eggs
		:parameters ()
		:precondition (and
						(in egga bowl)
                        (in eggb bowl)
					)
		:effect (and
                    (in eggs bowl)
					(increase (total-cost) 1)
		            )
	)
    (:action cook
		:parameters (?what - objects ?in - objects ?where - objects)
		:precondition (and
						(in eggs ?in)
                        (in salt ?in)
                        (in pepper ?in)
                        (is-stirred ?in)
						(is-on ?where)
						(is-at ?in ?where)
						(cook-triple ?what ?in ?where)
					)
		:effect (and
		            (cooked ?what)
					(increase (total-cost) 1)
		            )
	)
    (:action shake
		:parameters (?what - objects)
		:precondition (and
							(is-at ?what hands)
							(shake-single ?what)
						)
		:effect (and
		            (shaken ?what)
					(increase (total-cost) 1)
		            )
	)
    (:action cut
    	:parameters (?what - objects ?where - objects)
        :precondition (and
                        (is-at knife hands)
                        (cut-pair ?what ?where)
                        (imply
                            (or (= ?what carrot)
                                (= ?what cucumber))
                            (is-peeled?what))
                    )
        :effect (and
                    (when
                        (and 
                            (= ?what celery)
                            (not (is-at celery_pieceb ?where))
                            (not (is-at celery_piecec ?where))
                            (not (is-at celery_pieced ?where)))
                        (is-at celery_piecea ?where))
                    (when
                        (and 
                            (= ?what celery)
                            (is-at celery_piecea ?where)
                            (not (is-at celery_piecec ?where))
                            (not (is-at celery_pieced ?where)))
                        (is-at celery_pieceb ?where))
                    (when
                        (and 
                            (= ?what celery)
                            (is-at celery_piecea ?where)
                            (is-at celery_pieceb ?where)
                            (not (is-at celery_pieced ?where)))
                        (is-at celery_piecec ?where))
                    (when
                        (and 
                            (= ?what celery)
                            (is-at celery_piecea ?where)
                            (is-at celery_pieceb ?where)
                            (is-at celery_piecec ?where))
                        (is-at celery_pieced ?where))
                    (when
                        (and 
                            (= ?what carrot)
                            (not (is-at carrot_pieceb ?where))
                            (not (is-at carrot_piecec ?where))
                            (not (is-at carrot_pieced ?where)))
                        (is-at carrot_piecea ?where))
                    (when
                        (and 
                            (= ?what carrot)
                            (is-at carrot_piecea ?where)
                            (not (is-at carrot_piecec ?where))
                            (not (is-at carrot_pieced ?where)))
                        (is-at carrot_pieceb ?where))
                    (when
                        (and 
                            (= ?what carrot)
                            (is-at carrot_piecea ?where)
                            (is-at carrot_pieceb ?where)
                            (not (is-at carrot_pieced ?where)))
                        (is-at carrot_piecec ?where))
                    (when
                        (and 
                            (= ?what carrot)
                            (is-at carrot_piecea ?where)
                            (is-at carrot_pieceb ?where)
                            (is-at carrot_piecec ?where))
                        (is-at carrot_pieced ?where))
                    (when
                        (and 
                            (= ?what cucumber)
                            (not (is-at cucumber_pieceb ?where))
                            (not (is-at cucumber_piecec ?where))
                            (not (is-at cucumber_pieced ?where)))
                        (is-at cucumber_piecea ?where))
                    (when
                        (and 
                            (= ?what cucumber)
                            (is-at cucumber_piecea ?where)
                            (not (is-at cucumber_piecec ?where))
                            (not (is-at cucumber_pieced ?where)))
                        (is-at cucumber_pieceb ?where))
                    (when
                        (and 
                            (= ?what cucumber)
                            (is-at cucumber_piecea ?where)
                            (is-at cucumber_pieceb ?where)
                            (not (is-at cucumber_pieced ?where)))
                        (is-at cucumber_piecec ?where))
                    (when
                        (and 
                            (= ?what cucumber)
                            (is-at cucumber_piecea ?where)
                            (is-at cucumber_pieceb ?where)
                            (is-at cucumber_piecec ?where))
                        (is-at cucumber_pieced ?where))
                    (when
                        (and 
                            (= ?what salami)
                            (not (is-at salami_pieceb ?where))
                            (not (is-at salami_piecec ?where))
                            (not (is-at salami_pieced ?where)))
                        (is-at salami_piecea ?where))
                    (when
                        (and 
                            (= ?what salami)
                            (is-at salami_piecea ?where)
                            (not (is-at salami_piecec ?where))
                            (not (is-at salami_pieced ?where)))
                        (is-at salami_pieceb ?where))
                    (when
                        (and 
                            (= ?what salami)
                            (is-at salami_piecea ?where)
                            (is-at salami_pieceb ?where)
                            (not (is-at salami_pieced ?where)))
                        (is-at salami_piecec ?where))
                    (when
                        (and 
                            (= ?what salami)
                            (is-at salami_piecea ?where)
                            (is-at salami_pieceb ?where)
                            (is-at salami_piecec ?where))
                        (is-at salami_pieced ?where))
					(increase (total-cost) 1)
                )
    )
    (:action peel
    	:parameters (?what - objects ?with - objects)
        :precondition (and
                     	(is-at ?with hands)
                        (is-at ?what hands)
                        (peel-pair ?what ?with)
                    )
        :effect (and
                    (is-peeled ?what)
					(increase (total-cost) 1)
                )
    )
    (:action unroll
    	:parameters (?what - objects)
        :precondition (and
                        (in pizza_dough baking_pan)
                        (unroll-single ?what)
                        (or
                            (is-at baking_pan counter)
                            (is-at baking_pan stove))
                    )
        :effect (and
                    (is-unrolled ?what)
					(increase (total-cost) 1)
                )
    )
    (:action rinse
    	:parameters (?what - objects)
        :precondition (and
                        (att counter_place)
                        (rinse-single ?what)
                        (is-at ?what hands)
                    )
        :effect (and (is-rinsed ?what)
		             (increase (total-cost) 1)
                )
    )
    (:action make-pizza
    	:parameters ()
        :precondition (and
                        (is-at baking_pan oven)
                        (not (is-open oven))
                        (is-on oven)
                        (in cheese baking_pan)
                        (is-at salami_piecea baking_pan)
                        (in pizza_sauce baking_pan)
                        (is-unrolled pizza_dough)
                        (not (is-at pizza_dough_container hands))
                        (not (is-at pizza_sauce_can hands))
                        (not (is-at pizza_sauce_can_cap hands))
                        (not (is-at cheese_wrapper hands))
                        (not (is-at cheese hands))
                        (not (is-at salad_wrapper hands))
                        (not (is-at salami hands))
                        (not (is-at cheese_grater hands))
                        (not (is-at knife hands))
                        (not (is-open cupboard_br))
                        (not (is-open cupboard_bl))
                        (not (is-open cupboard_tr))
                        (not (is-open cupboard_tm))
                        (not (is-open cupboard_tl))
                        (not (is-open drawer))
                        (not (is-open fridge))
                    )
        :effect (and
                    (made_pizza)
					(increase (total-cost) 1)
                )
    )
    (:action make-salad
    	:parameters ()
        :precondition (and
                        (is-at carrot_piecea bowl)
                        (is-at celery_piecea bowl)
                        (is-at cucumber_piecea bowl)
                        (is-at salad bowl)
                        (in crouton bowl)
                        (in salad_sauce bowl)
                        (not (is-at knife hands))
                        (not (is-at peeler hands))
                        (not (is-open cupboard_br))
                        (not (is-open cupboard_bl))
                        (not (is-open cupboard_tr))
                        (not (is-open cupboard_tm))
                        (not (is-open cupboard_tl))
                        (not (is-open drawer))
                        (not (is-open fridge))
                        (not (is-open carrot_wrapper))
                        (not (is-open cucumber_wrapper))
                        (not (is-open celery_wrapper))
                        (not (is-open salad_wrapper))
                        (not (is-open salad_sauce_bottle))
                        (not (is-open crouton_wrapper))
                        (is-at salad_sauce_bottle fridge)
                        (is-at celery_wrapper fridge)
                        (is-at salad_wrapper fridge)
                        (is-at cucumber_wrapper fridge)
                        (is-at carrot_wrapper fridge)
                    )
        :effect (and
                    (made_salad)
					(increase (total-cost) 1)
                )
    )

    (:action make-sandwich
    	:parameters ()
        :precondition (and
                        (in jam breada)
                        (in peanut_butter breada)
                        (is-at breada plate)
                        (is-at breadb plate)
                        (not (is-at knife hands))
                        (not (is-at bread_bag hands))
                        (not (is-at plate hands))
                        (not (is-open cupboard_br))
                        (not (is-open cupboard_bl))
                        (not (is-open cupboard_tr))
                        (not (is-open cupboard_tm))
                        (not (is-open cupboard_tl))
                        (not (is-open drawer))
                        (not (is-open fridge))
                        (not (is-open bread_bag))
                        (not (is-open jam_glass))
                        (not (is-open peanut_butter_glass))
                        (is-at peanut_butter_glass fridge)
                        (is-at jam_glass fridge)
                    )
        :effect (and
                    (made_sandwich)
					(increase (total-cost) 1)
                )
    )
    (:action make-brownie
    	:parameters ()
        :precondition (and
                        (is-at baking_pan oven)
                        (not (is-open oven))
                        (in dough baking_pan)
                        (not (is-open cupboard_br))
                        (not (is-open cupboard_bl))
                        (not (is-open cupboard_tr))
                        (not (is-open cupboard_tm))
                        (not (is-open cupboard_tl))
                        (not (is-open drawer))
                        (not (is-open fridge))
                    )
        :effect (and
                    (made_brownie)
					(increase (total-cost) 1)
                )
    )
    (:action make-eggs
    	:parameters ()
        :precondition (and
                        (cooked eggs)
                        (in eggs plate)
                        (not (is-open cupboard_br))
                        (not (is-open cupboard_bl))
                        (not (is-open cupboard_tr))
                        (not (is-open cupboard_tm))
                        (not (is-open cupboard_tl))
                        (not (is-open drawer))
                        (not (is-open fridge))
                    )
        :effect (and
                    (made_eggs)
					(increase (total-cost) 1)
                )
    )
	 
)
