package iv.main;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.imageio.ImageIO;

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
	
	public static long getImageTakenTime(Path imagePath) throws ParseException, ImageReadException, IOException
	{
		final ImageMetadata metadata = Imaging.getMetadata(imagePath.toFile());

		if (!(metadata instanceof JpegImageMetadata))
		{
//			System.out.println("Not jpeg");
			return -1;
		}

		final JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

		final TiffField field = jpegMetadata.findEXIFValueWithExactMatch(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL);
		if (field == null)
		{
//			System.out.println(ExifTagConstants.EXIF_TAG_DATE_TIME_ORIGINAL.name + ": " + "Not Found.");
			return -1;
		}

		return DATE_FORMAT.parse(field.getValueDescription().replaceAll("'", "")).getTime();
	}

	public static long getLastModifiedTime(Path imageFile) throws IOException
	{
		return Files.getLastModifiedTime(imageFile).toMillis();
	}
	
	

	public static String checksumFile(Path path) throws NoSuchAlgorithmException, IOException
	{
		MessageDigest md = MessageDigest.getInstance(Settings.CHECKSUM_ALGORITHM);
		try (DigestInputStream dis = new DigestInputStream(Files.newInputStream(path), md))
		{
			byte[] buf = new byte[8192];
			while (dis.read(buf) > 0)
				;
		}
		byte[] digest = md.digest();
		StringBuilder sb = new StringBuilder(2 * digest.length);
		for (byte b : digest)
			sb.append(String.format("%02x", b));
		return sb.toString();
	}
	
	public static boolean isVertical (Path path) throws IOException
	{
		BufferedImage img = ImageIO.read(new File(path.toAbsolutePath().toString()));
		int width          = img.getWidth();
		int height         = img.getHeight();
		return height > width;
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