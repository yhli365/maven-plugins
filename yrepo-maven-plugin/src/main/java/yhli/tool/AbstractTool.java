package yhli.tool;

/**
 * @author yhli
 *
 */
public class AbstractTool {
	protected ToolLog log;

	public void setLog(ToolLog log) {
		this.log = log;
	}

	public ToolLog getLog() {
		return this.log;
	}

}
