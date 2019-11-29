package com.graphhopper.routing.util.parsers;

import com.graphhopper.reader.ReaderWay;
import com.graphhopper.routing.profiles.BooleanEncodedValue;
import com.graphhopper.routing.profiles.EnumEncodedValue;
import com.graphhopper.routing.profiles.GetOffBike;
import com.graphhopper.routing.profiles.RoadClass;
import com.graphhopper.routing.util.BikeFlagEncoder;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.storage.IntsRef;
import org.junit.Test;

import static org.junit.Assert.*;

public class OSMGetOffBikeParserTest {

    private EncodingManager em = EncodingManager.start().add(new BikeFlagEncoder()).build();
    private BooleanEncodedValue offBikeEnc = em.getBooleanEncodedValue(GetOffBike.KEY);
    private EnumEncodedValue<RoadClass> roadClassEnc = em.getEnumEncodedValue(RoadClass.KEY, RoadClass.class);

    @Test
    public void testHandleCommonWayTags() {
        ReaderWay way = new ReaderWay(1);
        way.setTag("highway", "steps");
        assertTrue(isGetOffBike(way));

        way.setTag("highway", "footway");
        assertTrue(isGetOffBike(way));

        way.setTag("highway", "footway");
        way.setTag("surface", "pebblestone");
        assertTrue(isGetOffBike(way));

        way.setTag("highway", "residential");
        assertFalse(isGetOffBike(way));

        way = new ReaderWay(1);
        way.setTag("highway", "residential");
        way.setTag("bicycle", "yes");
        assertFalse(isGetOffBike(way));

        way = new ReaderWay(1);
        way.setTag("highway", "cycleway");
        assertEquals(getRoadClass(way), RoadClass.CYCLEWAY);

        way.setTag("highway", "footway");
        way.setTag("bicycle", "yes");
        way.setTag("surface", "grass");
        assertFalse(isGetOffBike(way));

        way.setTag("bicycle", "designated");
        assertFalse(isGetOffBike(way));

        way = new ReaderWay(1);
        way.setTag("railway", "platform");
        assertTrue(isGetOffBike(way));

        way = new ReaderWay(1);
        way.setTag("highway", "secondary");
        way.setTag("bicycle", "dismount");
        assertTrue(isGetOffBike(way));

        way = new ReaderWay(1);
        way.setTag("highway", "platform");
        way.setTag("bicycle", "yes");
        assertFalse(isGetOffBike(way));

        way = new ReaderWay(1);
        way.setTag("highway", "track");
        way.setTag("foot", "yes");
        assertFalse(isGetOffBike(way));

        way = new ReaderWay(1);
        way.setTag("highway", "pedestrian");
        assertTrue(isGetOffBike(way));

        way = new ReaderWay(1);
        way.setTag("highway", "path");
        way.setTag("surface", "concrete");
        assertTrue(isGetOffBike(way));

        way = new ReaderWay(1);
        way.setTag("highway", "track");
        assertFalse(isGetOffBike(way));
    }

//    @Test
//    public void oldTest() {
//        // A footway is not of waytype get off the bike in case that it is part of a cycle route
//        ReaderRelation osmRel = new ReaderRelation(1);
//        ReaderWay osmWay = new ReaderWay(1);
//        osmWay.setTag("highway", "footway");
//        osmWay.setTag("surface", "grass");
//
//        // First tests without a cycle route relation, this is a get off the bike
//        IntsRef relFlags = encodingManager.handleRelationTags(osmRel, encodingManager.createRelationFlags());
//        String wayType = getWayTypeFromFlags(osmWay, relFlags);
//        assertEquals("get off the bike, unpaved", wayType);
//
//        // now as part of a cycle route relation
//        osmRel.setTag("type", "route");
//        osmRel.setTag("route", "bicycle");
//        osmRel.setTag("network", "lcn");
//        relFlags = encodingManager.handleRelationTags(osmRel, encodingManager.createRelationFlags());
//        wayType = getWayTypeFromFlags(osmWay, relFlags);
//        assertEquals("small way, unpaved", wayType);
//
//        // steps are still shown as get off the bike
//        osmWay.clearTags();
//        osmWay.setTag("highway", "steps");
//        relFlags = encodingManager.handleRelationTags(osmRel, encodingManager.createRelationFlags());
//        wayType = getWayTypeFromFlags(osmWay, relFlags);
//        assertEquals("get off the bike", wayType);
//
//        // Test for highway=platform.
//        osmRel.clearTags();
//        osmWay.clearTags();
//        osmWay.setTag("highway", "platform");
//
//        // First tests without a cycle route relation, this is a get off the bike
//        relFlags = encodingManager.handleRelationTags(osmRel, encodingManager.createRelationFlags());
//        wayType = getWayTypeFromFlags(osmWay, relFlags);
//        assertEquals("get off the bike", wayType);
//
//        // now as part of a cycle route relation
//        osmRel.setTag("type", "route");
//        osmRel.setTag("route", "bicycle");
//        osmRel.setTag("network", "lcn");
//        relFlags = encodingManager.handleRelationTags(osmRel, encodingManager.createRelationFlags());
//        wayType = getWayTypeFromFlags(osmWay, relFlags);
//        assertEquals("", wayType);
//    }

    private RoadClass getRoadClass(ReaderWay way) {
        IntsRef edgeFlags = em.handleWayTags(way, new EncodingManager.AcceptWay().put("bike", EncodingManager.Access.WAY), em.createRelationFlags());
        return roadClassEnc.getEnum(false, edgeFlags);
    }

    private boolean isGetOffBike(ReaderWay way) {
        IntsRef edgeFlags = em.handleWayTags(way, new EncodingManager.AcceptWay().put("bike", EncodingManager.Access.WAY), em.createRelationFlags());
        return offBikeEnc.getBool(false, edgeFlags);
    }
}