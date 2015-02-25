package tickets;



import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import de.zbit.jcmapper.fingerprinters.topological.Encoding2DECFP;
import de.zbit.jcmapper.io.reader.RandomAccessMDLReader;
import de.zbit.jcmapper.io.writer.ExporterFactory;
import de.zbit.jcmapper.io.writer.IExporter;
import de.zbit.jcmapper.io.writer.ExporterFactory.ExporterType;

public class Ticket3 {

	public IExporter exporter;
	
	@Before
	public void setUp() throws Exception{
		exporter = ExporterFactory.getExporter(ExporterType.STRING_PATTERNS);
	}
	
	@Test
	public void convertToStrings() throws IOException{
		exporter.export(new RandomAccessMDLReader(new File("./resources/Oxaceprol_MM.sdf")),
						new Encoding2DECFP(), "s_m_entry_id",
						new File(System.getProperty("java.io.tmpdir")+"/ticketXY.txt"),
						true);
	}
}
