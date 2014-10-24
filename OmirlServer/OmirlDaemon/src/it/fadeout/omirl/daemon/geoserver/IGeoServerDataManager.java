package it.fadeout.omirl.daemon.geoserver;


import java.io.IOException;
import java.net.MalformedURLException;

public interface IGeoServerDataManager {

	public static final String SHP_LAYER_ID_PREFIX = "SHP";

	/**
	 * aggiunge un layer WMS su geoserver
	 * @param sGSLayerName
	 * @param sGSNameSpace
	 * @param sGsFile
	 * @param sStyleId 
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public abstract void addLayer(String sGSLayerName, String sGSNameSpace,
			String sGsFile, String sStyleId) throws MalformedURLException,
			IOException;

	/**
	 * aggiunge un layer WMS su geoserver
	 * @param sGSLayerName
	 * @param sGSNameSpace
	 * @param sGsFile
	 * @param sStyleId 
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public abstract void addShapeLayer(String sGSLayerName,
			String sGSNameSpace, String sGsFile, String sStyleId)
			throws MalformedURLException, IOException;

	/**
	 * elimina un layer WMS su geoserver
	 * @param sDelGSLayerName
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public abstract void removeLayer(String sDelGSLayerName, String sNameSpace)
			throws MalformedURLException, IOException;

	public abstract void DeleteFile(String oFileName);

}