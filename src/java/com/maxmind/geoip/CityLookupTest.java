package com.maxmind.geoip;
/* CityLookupTest.java */

import java.io.IOException;

/* sample of how to use the GeoIP Java API with GeoIP City database */
/* Usage: java CityLookupTest 64.4.4.4 */

class CityLookupTest {
    public static void main(String[] args) {
	try {
	    LookupService cl = new LookupService(LookupService.GEOIP_DB_DIR + "GeoLiteCity.dat",
					LookupService.GEOIP_MEMORY_CACHE );
            Location l2 = cl.getLocation("85.136.212.161");
	    System.out.println("countryCode: " + l2.countryCode +
                               "\n countryName: " + l2.countryName +
                               "\n region: " + l2.region +
                               "\n regionName: " + RegionName.regionNameByCode(l2.countryCode, l2.region) +
                               "\n city: " + l2.city +
                               "\n postalCode: " + l2.postalCode +
                               "\n latitude: " + l2.latitude +
                               "\n longitude: " + l2.longitude +
 			       "\n dma code: " + l2.dma_code +
 			       "\n area code: " + l2.area_code +
                               "\n timezone: " + timeZone.timeZoneByCountryAndRegion(l2.countryCode, l2.region));

	    cl.close();
	}
	catch (IOException e) {
	    System.out.println("IO Exception");
	}
    }
}
