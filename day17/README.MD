A better way to do this would be by only considering active cubes.
* Get all active cubes
** Get their neighbours
** Count how many neighbours are active and if this amount is less than 3 and equal or larger than 2 keep it in a new keepAlive set
* Of all neighbours considered in the previous step (thus for all active cubes) but excluding the active cubes
** Get their neighbours and count the amount of active ones, if it is equal to 3 activate it and put it in the keepAlive set as well
* Use the keepAlive result set as input for consecutive cycles
