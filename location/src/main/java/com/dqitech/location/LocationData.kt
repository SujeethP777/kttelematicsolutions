package com.dqitech.location

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.Date
import java.util.UUID

open class LocationData(
    @PrimaryKey var id: String = UUID.randomUUID().toString(),
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var timestamp: Date = Date()
) : RealmObject()
