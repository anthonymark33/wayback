package org.archive.cdxserver.processor;

import org.archive.cdxserver.format.CDXFormat;
import org.archive.format.cdx.CDXLine;

import java.util.logging.Logger;

public abstract class RevisitResolver extends DupeCountProcessor {

	public final static String origfilename = "orig.filename";
	public final static String origoffset = "orig.offset";
	public final static String origlength = "orig.length";

        public final static Logger LOGGER = Logger.getLogger(RevisitResolver.class.getName());

        public RevisitResolver(BaseProcessor output, boolean showDupeCount) {
		super(output, showDupeCount);
	}
	
	protected static void fillBlankOrig(CDXLine line) {
	    LOGGER.warning("fillBlankOrig 'line' before processing: "+line);
	    line.setField(origlength, CDXLine.EMPTY_VALUE);
	    line.setField(origoffset, CDXLine.EMPTY_VALUE);
	    line.setField(origfilename, CDXLine.EMPTY_VALUE);
	    LOGGER.warning("fillBlankOrig 'line' after processing: "+line);
	}

	protected static void fillRevisit(CDXLine line, CDXLine origLine) {
	    LOGGER.warning("fillRevisit 'line' before processing: "+line+" origLine: "+origLine);
	    line.setMimeType(origLine.getMimeType());
	    line.setStatusCode(origLine.getStatusCode());
	    
	    line.setField(origlength, origLine.getLength());
	    line.setField(origoffset, origLine.getOffset());
	    line.setField(origfilename, origLine.getFilename());
	    LOGGER.warning("fillRevisit 'line' after processing: "+line+" origLine: "+origLine);
	}
    
	protected static boolean isRevisit(CDXLine line) {
		return ((CDXFormat)line.getNames()).isRevisit(line);
//		return (line.getMimeType().equals("warc/revisit") || line.getFilename()
//			.equals(CDXLine.EMPTY_VALUE));
	}
    
	@Override
	protected String[] extraFields() {
		return new String[] { origlength, origoffset, origfilename };
	}
	
	static abstract class RevisitTrack extends DupeTrack {
		abstract void revisit(CDXLine line);
		abstract void original(CDXLine line);
	}

	@Override
    protected void handleLine(DupeTrack track, CDXLine line) {
		RevisitTrack revisitTrack = (RevisitTrack)track;
		if (isRevisit(line)) {
			revisitTrack.revisit(line);
		} else {
			revisitTrack.original(line);
		}
	}
}
