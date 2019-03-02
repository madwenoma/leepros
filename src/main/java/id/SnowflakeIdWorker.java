package id;

/**
 * Twitter_Snowflake<br>
 * SnowFlake�Ľṹ����(ÿ������-�ֿ�):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 <br>
 * 1λ��ʶ������long����������Java���Ǵ����ŵģ����λ�Ƿ���λ��������0��������1������idһ�������������λ��0<br>
 * 41λʱ���(���뼶)��ע�⣬41λʱ��ز��Ǵ洢��ǰʱ���ʱ��أ����Ǵ洢ʱ��صĲ�ֵ����ǰʱ��� - ��ʼʱ���)
 * �õ���ֵ��������ĵĿ�ʼʱ��أ�һ�������ǵ�id��������ʼʹ�õ�ʱ�䣬�����ǳ�����ָ���ģ������������IdWorker���startTime���ԣ���41λ��ʱ��أ�����ʹ��69�꣬��T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10λ�����ݻ���λ�����Բ�����1024���ڵ㣬����5λdatacenterId��5λworkerId<br>
 * 12λ���У������ڵļ�����12λ�ļ���˳���֧��ÿ���ڵ�ÿ����(ͬһ������ͬһʱ���)����4096��ID���<br>
 * �������պ�64λ��Ϊһ��Long�͡�<br>
 * SnowFlake���ŵ��ǣ������ϰ���ʱ���������򣬲��������ֲ�ʽϵͳ�ڲ������ID��ײ(����������ID�ͻ���ID������)������Ч�ʽϸߣ������ԣ�SnowFlakeÿ���ܹ�����26��ID���ҡ�
 */
public class SnowflakeIdWorker {

    // ==============================Fields===========================================
    /** ��ʼʱ��� (2015-01-01) */
    private final long twepoch = 1420041600000L;

    /** ����id��ռ��λ�� */
    private final long workerIdBits = 5L;

    /** ���ݱ�ʶid��ռ��λ�� */
    private final long datacenterIdBits = 5L;

    /** ֧�ֵ�������id�������31 (�����λ�㷨���Ժܿ�ļ������λ�����������ܱ�ʾ�����ʮ������) */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /** ֧�ֵ�������ݱ�ʶid�������31 */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /** ������id��ռ��λ�� */
    private final long sequenceBits = 12L;

    /** ����ID������12λ */
    private final long workerIdShift = sequenceBits;

    /** ���ݱ�ʶid������17λ(12+5) */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /** ʱ���������22λ(5+5+12) */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /** �������е����룬����Ϊ4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** ��������ID(0~31) */
    private long workerId;

    /** ��������ID(0~31) */
    private long datacenterId;

    /** ����������(0~4095) */
    private long sequence = 0L;

    /** �ϴ�����ID��ʱ��� */
    private long lastTimestamp = -1L;

    //==============================Constructors=====================================
    /**
     * ���캯��
     * @param workerId ����ID (0~31)
     * @param datacenterId ��������ID (0~31)
     */
    public SnowflakeIdWorker(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    // ==============================Methods==========================================
    /**
     * �����һ��ID (�÷������̰߳�ȫ��)
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        //�����ǰʱ��С����һ��ID���ɵ�ʱ�����˵��ϵͳʱ�ӻ��˹����ʱ��Ӧ���׳��쳣
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //�����ͬһʱ�����ɵģ�����к���������
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //�������������
            if (sequence == 0) {
                //��������һ������,����µ�ʱ���
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        //ʱ����ı䣬��������������
        else {
            sequence = 0L;
        }

        //�ϴ�����ID��ʱ���
        lastTimestamp = timestamp;

        //��λ��ͨ��������ƴ��һ�����64λ��ID
        return ((timestamp - twepoch) << timestampLeftShift) //
                | (datacenterId << datacenterIdShift) //
                | (workerId << workerIdShift) //
                | sequence;
    }

    /**
     * ��������һ�����룬ֱ������µ�ʱ���
     * @param lastTimestamp �ϴ�����ID��ʱ���
     * @return ��ǰʱ���
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * �����Ժ���Ϊ��λ�ĵ�ǰʱ��
     * @return ��ǰʱ��(����)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    //==============================Test=============================================
    /** ���� */
    public static void main(String[] args) {
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        for (int i = 0; i < 1000; i++) {
            long id = idWorker.nextId();
            System.out.println(Long.toBinaryString(id));
            System.out.println(id);
        }
    }
}
