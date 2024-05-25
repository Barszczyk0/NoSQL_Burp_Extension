import burp.api.montoya.http.handler.HttpResponseReceived;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class MyTableModel extends AbstractTableModel
{
    private final List<HttpResponseReceived> log;

    public MyTableModel()
    {
        this.log = new ArrayList<>();
    }

    @Override
    public synchronized int getRowCount()
    {
        return log.size();
    }

    @Override
    public int getColumnCount()
    {
        return 6;
    }

    @Override
    public String getColumnName(int column)
    {
        return switch (column)
        {
            case 0 -> "#";
            case 1 -> "Method";
            case 2 -> "URL";
            case 3 -> "Body/Parameters";
            case 4 -> "Status Code";
            case 5 -> "Length";
            default -> "";
        };
    }

    @Override
    public synchronized Object getValueAt(int rowIndex, int columnIndex)
    {
        HttpResponseReceived responseReceived = log.get(rowIndex);
        return switch (columnIndex)
        {
            case 0 -> responseReceived.messageId();
            case 1 -> responseReceived.initiatingRequest().method();
            case 2 -> responseReceived.initiatingRequest().url();
            case 3 -> responseReceived.initiatingRequest().query().length() == 0 ?
                    responseReceived.initiatingRequest().body().toString() :
                    responseReceived.initiatingRequest().query();
            case 4 -> responseReceived.statusCode();
            case 5 -> responseReceived.toString().length();
            default -> "";
        };
    }

    public synchronized void add(HttpResponseReceived responseReceived)
    {
        int index = log.size();
        log.add(responseReceived);
        fireTableRowsInserted(index, index);
    }

    public synchronized HttpResponseReceived get(int rowIndex)
    {
        return log.get(rowIndex);
    }

    public void clear() {
        int rowCount = getRowCount();
        if (rowCount > 0) {
            log.clear();
            fireTableRowsDeleted(0, rowCount - 1);
        }
    }
}