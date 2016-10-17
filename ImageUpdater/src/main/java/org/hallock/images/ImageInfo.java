package org.hallock.images;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.ImageMetadata;
import org.apache.commons.imaging.common.ImageMetadata.ImageMetadataItem;
import org.apache.commons.imaging.common.RationalNumber;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.TiffImageMetadata;
import org.apache.commons.imaging.formats.tiff.constants.ExifTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.GpsTagConstants;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;
import org.apache.commons.imaging.formats.tiff.taginfos.TagInfo;

public class ImageInfo
{
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	private static final SimpleDateFormat DB_FORMAT = new SimpleDateFormat("yyyy/MM/dd/");
	
	
	private long originalDate;
	private long modifiedDate;
	private String fsPath; // Should be renamed...
	private String path; // relative to root
	private String timePath;
	
	
	
	public ImageInfo(long modifiedDate, String fsPath, String path)
	{
		this.modifiedDate = modifiedDate;
		this.fsPath = fsPath;
		this.path = path;
	}
	
	public void setOriginalDate(long originalDate, String timePath)
	{
		this.originalDate = originalDate;
		this.timePath = timePath;
	}
	
	
	public String toString()
	{
		return 
				  "[orig:" + new Date(originalDate) + "]"
				+ "[mod:" + new Date(modifiedDate) + "]"
				+ "[relPath:" + path + "]"
				+ "[fsPath:" + fsPath + "]"
				;
	}
	
	public long getImageDate()
	{
		return originalDate;
	}
	
	public long getModifiedTime()
	{
		return modifiedDate;
	}
	
	public String getPath()
	{
		return path;
	}

	public String getRoot()
	{
		return fsPath;
	}

	public String getTimePath()
	{
		return timePath;
	}
	
	
	public static ImageInfo readInitialImageInfo(Path root, Path path) throws ImageReadException, IOException, ParseException
	{
		path = path.toRealPath();
		
		return new ImageInfo(
				Files.getLastModifiedTime(path).toMillis(),
				root.toString(),
				root.relativize(path).toString());
	}
	

	public static void readRemainingImageInfo(Path root, Path path, ImageInfo info) throws ImageReadException, IOException, ParseException
	{
		path = path.toRealPath();
		
		final ImageMetadata metadata = Imaging.getMetadata(path.toFile());

		if (!(metadata instanceof JpegImageMetadata))
		{
			System.out.println("Not jpeg");
			throw new RuntimeException("Not jpeg...");
		}

		final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

		final TiffField field = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
		if (field == null)
		{
			System.out.println(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL.name + ": " + "Not Found.");
			throw new RuntimeException("No exif data...");
		}

		Date date = DATE_FORMAT.parse(field.getValueDescription().replaceAll("'", ""));
		String tPath = DB_FORMAT.format(date);
		
		info.setOriginalDate(date.getTime(), tPath);
	}
	
	


	    public static void metadataExample(final File file) throws ImageReadException,
	            IOException {
	        // get all metadata stored in EXIF format (ie. from JPEG or TIFF).
	        final ImageMetadata metadata = Imaging.getMetadata(file);

	        // System.out.println(metadata);

	        if (metadata instanceof JpegImageMetadata) {
	            final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

	            // Jpeg EXIF metadata is stored in a TIFF-based directory structure
	            // and is identified with TIFF tags.
	            // Here we look for the "x resolution" tag, but
	            // we could just as easily search for any other tag.
	            //
	            // see the TiffConstants file for a list of TIFF tags.

	            System.out.println("file: " + file.getPath());

	            // print out various interesting EXIF tags.
	            printTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_XRESOLUTION);
	            printTagValue(jpegMetadata, TiffTagConstants.TIFF_TAG_DATE_TIME);
	            printTagValue(jpegMetadata,
	                    ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
	            printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_DATE_TIME_DIGITIZED);
	            printTagValue(jpegMetadata, ExifTagConstants.EXIF_TAG_ISO);
	            printTagValue(jpegMetadata,
	                    ExifTagConstants.EXIF_TAG_SHUTTER_SPEED_VALUE);
	            printTagValue(jpegMetadata,
	                    ExifTagConstants.EXIF_TAG_APERTURE_VALUE);
	            printTagValue(jpegMetadata,
	                    ExifTagConstants.EXIF_TAG_BRIGHTNESS_VALUE);
	            printTagValue(jpegMetadata,
	                    GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
	            printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LATITUDE);
	            printTagValue(jpegMetadata,
	                    GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
	            printTagValue(jpegMetadata, GpsTagConstants.GPS_TAG_GPS_LONGITUDE);

	            System.out.println();

	            // simple interface to GPS data
	            final TiffImageMetadata exifMetadata = jpegMetadata.getExif();
	            if (null != exifMetadata) {
	                final TiffImageMetadata.GPSInfo gpsInfo = exifMetadata.getGPS();
	                if (null != gpsInfo) {
	                    final String gpsDescription = gpsInfo.toString();
	                    final double longitude = gpsInfo.getLongitudeAsDegreesEast();
	                    final double latitude = gpsInfo.getLatitudeAsDegreesNorth();

	                    System.out.println("    " + "GPS Description: "
	                            + gpsDescription);
	                    System.out.println("    "
	                            + "GPS Longitude (Degrees East): " + longitude);
	                    System.out.println("    "
	                            + "GPS Latitude (Degrees North): " + latitude);
	                }
	            }

	            // more specific example of how to manually access GPS values
	            final TiffField gpsLatitudeRefField = jpegMetadata.findEXIFValueWithExactMatch(
	                    GpsTagConstants.GPS_TAG_GPS_LATITUDE_REF);
	            final TiffField gpsLatitudeField = jpegMetadata.findEXIFValueWithExactMatch(
	                    GpsTagConstants.GPS_TAG_GPS_LATITUDE);
	            final TiffField gpsLongitudeRefField = jpegMetadata.findEXIFValueWithExactMatch(
	                    GpsTagConstants.GPS_TAG_GPS_LONGITUDE_REF);
	            final TiffField gpsLongitudeField = jpegMetadata.findEXIFValueWithExactMatch(
	                    GpsTagConstants.GPS_TAG_GPS_LONGITUDE);
	            if (gpsLatitudeRefField != null && gpsLatitudeField != null &&
	                    gpsLongitudeRefField != null &&
	                    gpsLongitudeField != null) {
	                // all of these values are strings.
	                final String gpsLatitudeRef = (String) gpsLatitudeRefField.getValue();
	                final RationalNumber gpsLatitude[] = (RationalNumber[]) (gpsLatitudeField.getValue());
	                final String gpsLongitudeRef = (String) gpsLongitudeRefField.getValue();
	                final RationalNumber gpsLongitude[] = (RationalNumber[]) gpsLongitudeField.getValue();

	                final RationalNumber gpsLatitudeDegrees = gpsLatitude[0];
	                final RationalNumber gpsLatitudeMinutes = gpsLatitude[1];
	                final RationalNumber gpsLatitudeSeconds = gpsLatitude[2];

	                final RationalNumber gpsLongitudeDegrees = gpsLongitude[0];
	                final RationalNumber gpsLongitudeMinutes = gpsLongitude[1];
	                final RationalNumber gpsLongitudeSeconds = gpsLongitude[2];

	                // This will format the gps info like so:
	                //
	                // gpsLatitude: 8 degrees, 40 minutes, 42.2 seconds S
	                // gpsLongitude: 115 degrees, 26 minutes, 21.8 seconds E

	                System.out.println("    " + "GPS Latitude: "
	                        + gpsLatitudeDegrees.toDisplayString() + " degrees, "
	                        + gpsLatitudeMinutes.toDisplayString() + " minutes, "
	                        + gpsLatitudeSeconds.toDisplayString() + " seconds "
	                        + gpsLatitudeRef);
	                System.out.println("    " + "GPS Longitude: "
	                        + gpsLongitudeDegrees.toDisplayString() + " degrees, "
	                        + gpsLongitudeMinutes.toDisplayString() + " minutes, "
	                        + gpsLongitudeSeconds.toDisplayString() + " seconds "
	                        + gpsLongitudeRef);

	            }

	            System.out.println();

	            final List<ImageMetadataItem> items = jpegMetadata.getItems();
	            for (int i = 0; i < items.size(); i++) {
	                final ImageMetadataItem item = items.get(i);
	                System.out.println("    " + "item: " + item);
	            }

	            System.out.println();
	        }
	    }

	    private static void printTagValue(final JpegImageMetadata jpegMetadata,
	            final TagInfo tagInfo) {
	        final TiffField field = jpegMetadata.findEXIFValueWithExactMatch(tagInfo);
	        if (field == null) {
	            System.out.println(tagInfo.name + ": " + "Not Found.");
	        } else {
	            System.out.println(tagInfo.name + ": "
	                    + field.getValueDescription());
	        }
	    }



}
