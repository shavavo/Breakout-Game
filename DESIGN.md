Design
====
* The design of this game started in Main Game and branched off into different 
component classes. In MainGame.java, the game state is handled (exm. PLAYING, INTERMISSION, GAME_OVER). The main loop is 
the method step(), which reacts to the game state, and updates any components that are shown. The main loop also changes
the game state. For example, if there are no more blocks left the game is changed from PLAYING to INTERMISSION, which 
indicates the level is won and prepare for the next one.

* To add new features, you can go to the specific class you want to modify, and add methods. Then, modify the main game
loop to update your new/existing class. It is completely fine to first develop in the main loop for rapid testing and 
prototyping, but move methods to where they belong after.

* A design choice I made was to standardize the update() function across Bouncer, Drop, and Laser. All three of 
these classes implement UpdateableObject. As a result of this, the method calls to update are all the same, and very 
simple. In the main class, I am able to update all bouncers, drops, and lasers with:

```java
private void updateObjectList(List<? extends UpdateableObject> objectsToUpdate, double elapsedTime) {
    List<UpdateableObject> toRemove = new ArrayList<>();
    for(UpdateableObject object : objectsToUpdate) {
        boolean shouldRemove = object.update(elapsedTime);
        if(shouldRemove) toRemove.add(object);
    }

    objectsToUpdate.removeAll(toRemove);
}

updateObjectList(myBouncers, elapsedTime);
updateObjectList(myDrops, elapsedTime);
updateObjectList(myLasers, elapsedTime);

```
* The code above loops through the lists of UpdateableObjects and calls update on them, and then remove any that need to 
be removed. This made the code cleaner on this part but also required me to pass MainGame as a context into each of the 
classes and use getters instead of parameters.

* Another design choice I made was to store most types of state in enums, which I think turned out well because it made 
the code more readable.