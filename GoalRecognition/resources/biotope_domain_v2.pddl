(define (domain biotope)
	(:requirements :strips)
	(:types
		Object
		User
		Location
		Time
		KitchenType
		Amount
		Longitude
		Latitude
	)
	(:predicates
		(FoundCity ?whereLon - Longitude ?whereLat - Latitude)
		(ParkingSpotReservation ?whereLon - Longitude ?whereLat - Latitude ?who - User)
		(RestaurantReservation ?whereLon - Longitude ?whereLat - Latitude ?when - Time ?who - User ?cuisine - KitchenType ?persons - Amount)
		(FoundParkingSite ?whereLon - Longitude ?whereLat - Latitude)
		(FoundFreeParkingSpot ?whereLon - Longitude ?whereLat - Latitude)
		(FoundRestaurant ?whereLon - Longitude ?whereLat - Latitude ?cuisine - KitchenType)
	)

	(:functions
		(cost)
	)
	
	(:action search_parking_site
		:parameters(?whereLon - Longitude ?whereLat - Latitude)
		:precondition(and
			(FoundCity ?whereLon ?whereLat)
		)
		:effect(and
			(FoundParkingSite ?whereLon ?whereLat)
			(increase (cost) 1)
		)
	)
	
	(:action search_City		
		:parameters(?whereLon - Longitude ?whereLat - Latitude)
		:precondition(and
		)
		:effect(and
			(FoundCity ?whereLon ?whereLat)
			(increase (cost) 1)
		)
	)
	
	(:action search_free_parking_spot
		:parameters(?whereLon - Longitude ?whereLat - Latitude)
		:precondition(and
			(FoundParkingSite ?whereLon ?whereLat)
		)
		:effect(and
			(FoundFreeParkingSpot ?whereLon ?whereLat)
			(increase (cost) 1)
		)
	)
	
	(:action reserve_free_parking_spot
		:parameters(?whereLon - Longitude ?whereLat - Latitude ?who - User)
		:precondition(and
			(FoundFreeParkingSpot ?whereLon ?whereLat)
		)
		:effect(and
			(not (FoundFreeParkingSpot ?whereLon ?whereLat))
			(not (FoundParkingSite ?whereLon ?whereLat))
			(ParkingSpotReservation ?whereLon ?whereLat ?who)
			(increase (cost) 1)
		)
	)
	
	(:action search_restaurant
		:parameters(?whereLon - Longitude ?whereLat - Latitude ?cuisine - KitchenType)
		:precondition(and (not (FoundRestaurant ?whereLon ?whereLat ?cuisine)))
		:effect(and
			(FoundRestaurant ?whereLon ?whereLat ?cuisine)
			(increase (cost) 1)
		)
	)
	
	(:action reserve_restaurant_table
		:parameters(?whereLon - Longitude ?whereLat - Latitude ?when - Time ?who - User ?cuisine - KitchenType ?persons - Amount)
		:precondition(and
			(FoundRestaurant ?whereLon ?whereLat ?cuisine)
		)
		:effect(and
			(not (FoundRestaurant ?whereLon ?whereLat ?cuisine))
			(RestaurantReservation ?whereLon ?whereLat ?when ?who ?cuisine ?persons)
			(increase (cost) 1)
		)
	)
	
	
)