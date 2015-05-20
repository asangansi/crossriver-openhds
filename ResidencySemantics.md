# Residency Semantics #

Residencies in the CRHDS are critical for a number of reasons. For example, residencies are used in generating demographic reports. It's important the CRHDS system maintains an accurate representation of residencies for the individuals registered in the system.

### Definition ###
A residency indicates an individual is living at a location. The CRHDS requires an individual can have only **one** residency at a time. This residency is referred to as the individuals **current residency**. It's possible that an individual will have many residencies over some time period, but at any given point in time there can only be one **current residency** for the individual.

### Semantics ###

A residency can be started in 3 ways:
  * The baseline census (enumeration)
  * Through an in migration event
  * Through a pregnancy outcome event

A residency can be ended in 2 ways:
  * Through an out migration event
  * Through a death event

Since the CRHDS is responsible for maintaining the constraint of **one residency at a time** for an individual, users cannot arbitrarily modify residencies through the CRHDS application. Instead, they must modify the events that are associated with the residency.

**Enumeration**

If a residency has a start type of enumeration, users are allowed to modify the residency start date and location.

**In Migration**

If a residency has a start type of in migration, users cannot modify the residency record directly. Instead, they must modify the in migration event, and those changes will propagate back to the residency record. Specifically, users can expect the following fields to update the residency:
  * In Migration Recorded date -> Residency Start date
  * In Migration House Id -> Residency Location
  * In Migration Field Worker -> Residency Field Worker

**Pregnancy Outcome (Birth)**

Similarly to in migration, if a residency has a start type of birth, users cannot modify the residency directly. They must modify the pregnancy outcome event, and those changes will propagate back to the residency. Specifically:
  * Pregnancy Outcome Recorded Date -> Residency Start Date
  * Pregnancy Outcome House -> Residency Location
  * Pregnancy Outcome Field Worker -> Residency Field Worker