package db.mysql.binlog;

public class Models {
}


class BinlogInfo {
    private String binlogName;
    private Long fileSize;
    // 省略Getter和Setter

    public BinlogInfo(String binlogName, Long fileSize) {
        this.binlogName = binlogName;
        this.fileSize = fileSize;
    }

    public String getBinlogName() {
        return binlogName;
    }

    public void setBinlogName(String binlogName) {
        this.binlogName = binlogName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
}

class BinlogMasterStatus {
    private String binlogName;
    private long position;
    // 省略Getter和Setter

    public String getBinlogName() {
        return binlogName;
    }

    public void setBinlogName(String binlogName) {
        this.binlogName = binlogName;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }
}

class ColumnInfo {
    private String name;
    private String type;

    public ColumnInfo(String name, String type) {
        this.name = name;
        this.type = type;
    }
// 省略Getter和Setter

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
