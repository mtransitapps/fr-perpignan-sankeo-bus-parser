package org.mtransit.parser.fr_perpignan_sankeo_bus;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRouteSNToIDConverter;

import java.util.List;
import java.util.Locale;

// https://transport.data.gouv.fr/datasets/gtfs-sankeo
public class PerpignanSankeoBusAgencyTools extends DefaultAgencyTools {

	public static void main(@NotNull String[] args) {
		new PerpignanSankeoBusAgencyTools().start(args);
	}

	@Nullable
	@Override
	public List<Locale> getSupportedLanguages() {
		return LANG_FR;
	}

	@NotNull
	public String getAgencyName() {
		return "Sankéo";
	}

	@Override
	public boolean defaultExcludeEnabled() {
		return true;
	}

	@NotNull
	@Override
	public Integer getAgencyRouteType() {
		return MAgency.ROUTE_TYPE_BUS;
	}

	@Override
	public boolean defaultRouteIdEnabled() {
		return true;
	}

	@Override
	public boolean useRouteShortNameForRouteId() {
		return true;
	}

	@Nullable
	@Override
	public Long convertRouteIdNextChars(@NotNull String nextChars) {
		switch (nextChars) {
		case "EXP":
			return MRouteSNToIDConverter.endsWith(MRouteSNToIDConverter.other(1L));
		}
		return super.convertRouteIdNextChars(nextChars);
	}

	@Nullable
	@Override
	public Long convertRouteIdFromShortNameNotSupported(@NotNull String routeShortName) {
		switch (routeShortName) {
		case "A":
			return MRouteSNToIDConverter.other(1L);
		case "B":
			return MRouteSNToIDConverter.other(2L);
		case "C":
			return MRouteSNToIDConverter.other(3L);
		case "CANET":
			return MRouteSNToIDConverter.other(10L);
		case "CANET'ON D'ETE":
			return MRouteSNToIDConverter.other(11L);
		case "NAV RAYONNANTES":
			return MRouteSNToIDConverter.other(140L);
		case "NCMI":
			return MRouteSNToIDConverter.other(141L);
		case "NAV DECOUVERTE":
			return MRouteSNToIDConverter.other(142L);
		case "NITB":
			return MRouteSNToIDConverter.other(143L);
		case "NCAN":
			return MRouteSNToIDConverter.other(144L);
		case "NPTB":
			return MRouteSNToIDConverter.other(145L);
		case "P'TIT BUS":
			return MRouteSNToIDConverter.other(160L);
		}
		return super.convertRouteIdFromShortNameNotSupported(routeShortName);
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@Override
	public boolean defaultAgencyColorEnabled() {
		return true;
	}

	// private static final String AGENCY_COLOR = "753BBD"; // PURPLE from website CSS
	private static final String AGENCY_COLOR = "00A9CE"; // BLUE from website CSS

	@NotNull
	@Override
	public String getAgencyColor() {
		return AGENCY_COLOR;
	}

	@Override
	public boolean directionFinderEnabled() {
		return true;
	}

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.removeVia(tripHeadsign);
		tripHeadsign = CleanUtils.SAINT.matcher(tripHeadsign).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.cleanStreetTypesFRCA(gStopName);
		return CleanUtils.cleanLabelFR(gStopName);
	}

	@Override
	public int getStopId(@NotNull GStop gStop) {
		//noinspection deprecation
		String stopId = gStop.getStopId();
		if (!CharUtils.isDigitsOnly(stopId)) {
			return Math.abs(stopId.hashCode()); // FIXME WTF!!!!
		}
		return Integer.parseInt(stopId);
	}
}
