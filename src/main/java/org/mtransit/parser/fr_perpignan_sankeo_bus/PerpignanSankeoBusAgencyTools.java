package org.mtransit.parser.fr_perpignan_sankeo_bus;

import static org.mtransit.commons.RegexUtils.BEGINNING;
import static org.mtransit.commons.RegexUtils.DIGIT_CAR;
import static org.mtransit.commons.RegexUtils.END;
import static org.mtransit.commons.RegexUtils.atLeastOne;
import static org.mtransit.commons.RegexUtils.group;
import static org.mtransit.commons.RegexUtils.mGroup;
import static org.mtransit.commons.RegexUtils.or;
import static org.mtransit.parser.Constants.EMPTY;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mtransit.commons.CharUtils;
import org.mtransit.commons.CleanUtils;
import org.mtransit.parser.DefaultAgencyTools;
import org.mtransit.parser.MTLog;
import org.mtransit.parser.gtfs.data.GStop;
import org.mtransit.parser.mt.data.MAgency;
import org.mtransit.parser.mt.data.MRouteSNToIDConverter;

import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

// https://transport.data.gouv.fr/datasets/gtfs-sankeo
public class PerpignanSankeoBusAgencyTools extends DefaultAgencyTools {

	private static final boolean FF_USING_ROUTE_ID_DEFAULT_CONVERTER = false;
	// private static final boolean FF_USING_ROUTE_ID_DEFAULT_CONVERTER = true;

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
		if (FF_USING_ROUTE_ID_DEFAULT_CONVERTER) {
			return super.convertRouteIdNextChars(nextChars);
		}
		switch (nextChars) {
		case "EXP":
			return MRouteSNToIDConverter.endsWith(MRouteSNToIDConverter.other(1L));
		case "EX":
			return MRouteSNToIDConverter.endsWith(MRouteSNToIDConverter.other(2L));
		}
		return super.convertRouteIdNextChars(nextChars);
	}

	private static final Pattern SCHOOL_RID_ = Pattern.compile(
			group(atLeastOne(DIGIT_CAR)) + "P" + group(atLeastOne(DIGIT_CAR))
			, Pattern.CASE_INSENSITIVE);
	private static final String SCHOOL_RID_REPLACEMENT = mGroup(1) + "000" + mGroup(2);

	@Nullable
	@Override
	public Long convertRouteIdFromShortNameNotSupported(@NotNull String routeShortName) {
		if (FF_USING_ROUTE_ID_DEFAULT_CONVERTER) {
			return MRouteSNToIDConverter.defaultConverter(routeShortName);
		}
		switch (routeShortName) {
		case "A":
			return 1000L;
		case "B":
			return 2000L;
		case "C":
			return 3000L;
		case "D":
			return 4000L;
		case "CANET":
			return 9000L;
		case "CANET'ON D'ETE":
			return 9001L;
		case "NAV RAYONNANTES":
			return 9002L;
		case "NCMI":
			return 9003L;
		case "NAV DECOUVERTE":
			return 9004L;
		case "NITB":
			return 9005L;
		case "NCAN":
			return 9006L;
		case "NPTB":
			return 9007L;
		case "P'TIT BUS":
			return 9008L;
		case "NWIL":
			return 9009L;
		case "NRAY":
			return 9010L;
		case "NDEC":
			return 9011L;
		case "NINT":
			return 9012L;
		case "NCSU":
			return 9013L;
		}
		try {
			return Long.parseLong(SCHOOL_RID_.matcher(routeShortName).replaceAll(SCHOOL_RID_REPLACEMENT));
		} catch (NumberFormatException nfe) {
			MTLog.logNonFatal(nfe);
		}
		return super.convertRouteIdFromShortNameNotSupported(routeShortName);
	}

	@Override
	public boolean defaultRouteLongNameEnabled() {
		return true;
	}

	@Override
	public @NotNull String cleanRouteLongName(@NotNull String routeLongName) {
		routeLongName = CleanUtils.toLowerCaseUpperCaseWords(getFirstLanguageNN(), routeLongName, getWords());
		routeLongName = CleanUtils.cleanLabelFR(routeLongName);
		return super.cleanRouteLongName(routeLongName);
	}

	private String[] getWords() {
		return new String[]{
				"TGV", "SNCF",
				"HLM", "IUT",
				"PE", "BFM",
				"LEPA", "CFA",
				"liO",
		};
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

	@Override
	public boolean allowNonDescriptiveHeadSigns(long routeId) {
		if (routeId > 20_000) { // School routes
			return true;
		}
		return super.allowNonDescriptiveHeadSigns(routeId);
	}

	private static final Pattern ALLER_RETOUR = Pattern.compile(
			BEGINNING + group(or("aller", "retour") + END
			), Pattern.CASE_INSENSITIVE);

	private static final Pattern AM_ = Pattern.compile(group("AM"), Pattern.CASE_INSENSITIVE);
	private static final String AM_REPLACEMENT = "Matin";

	private static final Pattern PM_ = Pattern.compile(group("PM"), Pattern.CASE_INSENSITIVE);
	private static final String PM_REPLACEMENT = "Après-Midi";

	@NotNull
	@Override
	public String cleanTripHeadsign(@NotNull String tripHeadsign) {
		tripHeadsign = CleanUtils.toLowerCaseUpperCaseWords(getFirstLanguageNN(), tripHeadsign, getWords());
		tripHeadsign = ALLER_RETOUR.matcher(tripHeadsign).replaceAll(EMPTY);
		tripHeadsign = AM_.matcher(tripHeadsign).replaceAll(AM_REPLACEMENT);
		tripHeadsign = PM_.matcher(tripHeadsign).replaceAll(PM_REPLACEMENT);
		tripHeadsign = CleanUtils.removeVia(tripHeadsign);
		tripHeadsign = CleanUtils.SAINT.matcher(tripHeadsign).replaceAll(CleanUtils.SAINT_REPLACEMENT);
		tripHeadsign = CleanUtils.cleanStreetTypesFRCA(tripHeadsign);
		return CleanUtils.cleanLabelFR(tripHeadsign);
	}

	@NotNull
	@Override
	public String cleanStopName(@NotNull String gStopName) {
		gStopName = CleanUtils.toLowerCaseUpperCaseWords(getFirstLanguageNN(), gStopName, getWords());
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
