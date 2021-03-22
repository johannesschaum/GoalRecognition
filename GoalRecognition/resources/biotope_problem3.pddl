(define (problem biotopeDomain)
	(:domain biotope)
	(:objects
		one two three four five six seven - Amount
		eight nine ten eleven twelve thirteen fourteen fiveteen sixteen seventeen eighteen nineteen twenty - Time
		A B C D E F G H I J K L - Location
		lon1 lon2 lon3 lon4 lon5 lon6 lon7 - Longitude
		lat1 lat2 lat3 lat4 lat5 lat6 lat7 - Latitude
		german chinese greek italian - KitchenType
		biotopeUser - User
	)
	(:init
		(= (cost) 0)
	)
	(:goal 
		(and (ParkingSpotReservation lon5 lat6 biotopeUser)
		(RestaurantReservation lon5 lat6 nine biotopeUser greek two))
	)
	(:metric minimize (cost))
)