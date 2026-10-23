package com.sinogale.dksdkdemo.config;

import java.util.Arrays;
import java.util.List;

/**
 * @Author: wenzhi
 * @Date: 2024/01/30
 */
public class ConfigKeys {
    public static final int batchSize = 20000;

    public static final String ODPS_DB_Type = "MaxCompute/ODPS";

    public static final String MYSQL_DB_Type = "MySQL";

    public static final String GW_CODE = "11101";

    public static final String GW_CODE_RDS = "9999";

    public static final String GW_CODE_NAME = "国网";

    public static final String REQUEST_PACKAGE = "com.aliyun.dataworks_20240902.models.";

    public static final String REGIONID = "default";

    public static final String STAT_FREQ_Y = "01";
    public static final String STAT_FREQ_Q = "03";
    public static final String STAT_FREQ_M = "04";
    public static final String STAT_FREQ_D = "07";

    public static final String DFHS_INDEX_NO = "01010001";

    public static final List<String> DAY_UPDATE_INDEX_ARR = Arrays.asList("01010001");

    public static final String DYJS_INDEX_NO = "01010002";
    public static final String YK_INDEX_NO = "03010001";
    public static final String XS_INDEX_NO = "02020001";
    public static final String CJ_INDEX_NO = "02020002";


    private static final String INDEX_DATE_ALL_FEILD = "index_no,index_name,index_value,stat_date,mgt_org_code,mgt_org_name,dist_lv,stat_freq,mom,yoy,peer_mgt_org_prov,rank_mgt_org_prov,peer_avg_value_prov,peer_max_value_prov,peer_min_value_prov,peer_mgt_org_last,rank_mgt_org_last,peer_avg_value_last,peer_max_value_last,peer_min_value_last,valid_flag,ds,province_code,index_id";

    private static final String INDEX_DATE_PART_FEILD = "index_no,index_name,index_value,stat_date,mgt_org_code,mom,yoy,stat_freq,index_id";

    public static final String I_SCHYYPT_TABLE = "INSERT INTO %s (" + INDEX_DATE_ALL_FEILD + ") values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    public static final String INSERT_PRE = "INSERT INTO %s (" + INDEX_DATE_ALL_FEILD + ") values ";

    public static final String Q_SYNC_SCHYYPT_TABLE = "select " + INDEX_DATE_ALL_FEILD + " from( SELECT index_no, index_name, index_value, stat_date, mgt_org_code, mgt_org_name, dist_lv, stat_freq, mom, yoy, peer_mgt_org_prov, rank_mgt_org_prov, peer_avg_value_prov, peer_max_value_prov, peer_min_value_prov, peer_mgt_org_last, rank_mgt_org_last, peer_avg_value_last, peer_max_value_last, peer_min_value_last, valid_flag, stat_date AS ds, case when provincial_id= '10500001' then '11102' when provincial_id='10000028' then '12101' when provincial_id='10372269' then '13102' when provincial_id= '10687637' then '16101' when provincial_id= '10583026' then '14101' when provincial_id= '10045073' then '15101' when provincial_id= '10130001' then '21102' when provincial_id= '10200000' then '22101' when provincial_id= '10400000' then '23101' when provincial_id= '10010340' then '31102' when provincial_id= '10430001' then '32101' when provincial_id= '10262690' then '33101' when provincial_id= '10330027' then '34101' when provincial_id= '10220116' then '35101' when provincial_id= '10090121' then '36101' when provincial_id= '10554019' then '37101' when provincial_id= '10522001' then '41101' when provincial_id= '10467828' then '42102' when provincial_id= '10175327' then '43101' when provincial_id= '10060001' then '50101' when provincial_id= '10300001' then '51101' when provincial_id= '10520001' then '54101' when provincial_id= '10750029' then '61102' when provincial_id= '10778300' then '62101' when provincial_id= '10798565' then '63101' when provincial_id= '10606629' then '64101' when provincial_id= '10621001' then '65101' END AS province_code,index_id from pro_ads_cdm_prd.%s where ds= '%s' and stat_date= '%s' AND dist_lv= '03' union all SELECT index_no, index_name, index_value, stat_date, mgt_org_code, mgt_org_name, dist_lv, stat_freq, mom, yoy, 27 AS peer_mgt_org_prov, row_number() over(partition by index_no  order by index_value desc) AS rank_mgt_org_prov, AVG(index_value) over(PARTITION BY index_no) AS peer_avg_value_prov, MAX(index_value) over(PARTITION BY index_no)  AS peer_max_value_prov, MIN(index_value) over(PARTITION BY index_no)  AS peer_min_value_prov, 27 AS peer_mgt_org_last, row_number() over(partition by index_no order by index_value desc) AS rank_mgt_org_last, AVG(index_value) over(PARTITION BY index_no) AS peer_avg_value_last, MAX(index_value) over(PARTITION BY index_no) AS peer_max_value_last, MIN(index_value)  over(PARTITION BY index_no)  AS peer_min_value_last, valid_flag, stat_date AS ds, case when provincial_id= '10500001' then '11102' when provincial_id= '10000028' then '12101' when provincial_id= '10372269' then '13102' when provincial_id= '10687637' then '16101' when provincial_id= '10583026' then '14101' when provincial_id= '10045073' then '15101' when provincial_id= '10130001' then '21102' when provincial_id= '10200000' then '22101' when provincial_id= '10400000' then '23101' when provincial_id= '10010340' then '31102' when provincial_id= '10430001' then '32101' when provincial_id= '10262690' then '33101' when provincial_id= '10330027' then '34101' when provincial_id= '10220116' then '35101' when provincial_id= '10090121' then '36101' when provincial_id= '10554019' then '37101' when provincial_id= '10522001' then '41101' when provincial_id= '10467828' then '42102' when provincial_id= '10175327' then '43101' when provincial_id= '10060001' then '50101' when provincial_id= '10300001' then '51101' when provincial_id= '10520001' then '54101' when provincial_id= '10750029' then '61102' when provincial_id= '10778300' then '62101' when provincial_id= '10798565' then '63101' when provincial_id= '10606629' then '64101' when provincial_id= '10621001' then '65101' END AS province_code,index_id from pro_ads_cdm_prd.%s where ds='%s' and stat_date= '%s' AND dist_lv= '02') order by province_code,ds,index_no,mgt_org_code";

    public static final String Q_SYNC_SCHYYPT_TABLE_02 = "insert overwrite table pro_ads_cms2_prod.%s partition(provincial_id, ds) SELECT index_no,index_name,index_value,stat_date,mgt_org_code,mgt_org_name,dist_lv,stat_freq,mom,yoy,27 AS peer_mgt_org_prov,row_number() over(partition by index_no order by index_value desc) AS rank_mgt_org_prov,AVG(index_value)  over(PARTITION BY index_no) AS peer_avg_value_prov,MAX(index_value) over(PARTITION BY index_no) AS peer_max_value_prov,MIN(index_value) over(PARTITION BY index_no) AS peer_min_value_prov,27 AS peer_mgt_org_last,row_number() over(partition by index_no order by index_value desc) AS rank_mgt_org_last,AVG(index_value) over(PARTITION BY index_no) AS peer_avg_value_last,MAX(index_value) over(PARTITION BY index_no) AS peer_max_value_last,MIN(index_value) over(PARTITION BY index_no) AS peer_min_value_last,valid_flag,standby_1,standby_2,standby_3,standby_4,standby_5,standby_6,standby_7,standby_8,standby_9,standby_10,index_id,provincial_id,ds from pro_ads_cdm_prd.%<s where ds= '%s' and stat_date= '%s' and dist_lv= '02'";

    public static final String DEL_SYNC_SCHYYPT_TABLE = "delete from %s where stat_date='%s'";

    public static final String INDEX_CALC_CONFIG_QUERY = "SELECT index_no,index_name,stat_freq,idx_fmla,idx_type,index_id FROM ads_cst_cst_emss_index_calc_config where valid_flag='01' ORDER BY idx_type ";

    public static final String INDEX_DATE_QUERY = "select " + INDEX_DATE_PART_FEILD + " from %s WHERE  stat_date in (%s) AND dist_lv IN('01','02')";

    public static final String DEL_36_TBL_SQL = "delete from %s where stat_date='%s' and index_no in (%s) and mgt_org_code='" + ConfigKeys.GW_CODE + "'";

    public static final String Q_INDEX_FILE_SQL = "select fid,affiliation,index_no,index_name,index_frrmulas,association,association_index,data_source,base_dimension,statistical_cycle,sync_policy,target_value,index_type from ads_cst_wbql_emss_exam_index_file order by fid ";

    public static final String Q_CONFIG_SQL = "select root_node_id,job_name,project_env,node_name,node_params,include_node_ids,exclude_node_ids,start_biz_date,end_biz_date from dim_run_cycle_dag_nodes";


    //量价监测
    public static final String QTY_PRICE_MONITOR = "insert into ads_rpt_elec_qty_price_monitor_statistic(region,sort_num,org_no,org_name,stat_date,ddeal_price,serial_num37,qty_pre_accu_rto,dlgd_price,serial_num48,prc_pre_accu_rto,serial_number,create_time,isc)\n" +
            "SELECT  tt.region\n" +
            "        ,tt.serialNum sort_num\n" +
            "        ,tt.orgNo org_no\n" +
            "        ,tt.orgName org_name\n" +
            "        ,'%s' stat_date\n" +
            "        ,round(CAST(nvl(tt.purPrePower, 0) AS DECIMAL(18, 2)), 2) ddeal_price\n" +
            "        ,round(CAST(nvl(tt.sumqty, 0) AS DECIMAL(18, 2)), 2) serial_num37\n" +
            "        ,round(CAST(nvl(tt.preAccuracyBattery, 0) AS DECIMAL(18, 2)), 2) qty_pre_accu_rto\n" +
            "        ,round(CAST(nvl(tt.preAverPrice, 0) AS DECIMAL(18, 2)), 2) dlgd_price\n" +
            "        ,round(CAST(nvl(nullif(tt.avgprice, 0), 0) AS DECIMAL(18, 2)), 2) serial_num48\n" +
            "        ,round(CAST(nvl(nullif(tt.preAccuracyPrice, 0), 0) AS DECIMAL(18, 2)),2) prc_pre_accu_rto\n" +
            "        ,'%s' AS serial_number\n" +
            "        ,'%s' AS create_time\n" +
            "        ,'%s' AS isc\n" +
            "FROM    (\n" +
            "            SELECT  power.region region\n" +
            "                    ,CASE    WHEN elecp.orgNo = 11102 THEN 1\n" +
            "                             WHEN elecp.orgNo = 12101 THEN 2\n" +
            "                             WHEN elecp.orgNo = 13102 THEN 3\n" +
            "                             WHEN elecp.orgNo = 16101 THEN 4\n" +
            "                             WHEN elecp.orgNo = 14101 THEN 5\n" +
            "                             WHEN elecp.orgNo = 37101 THEN 6\n" +
            "                             WHEN elecp.orgNo = 31102 THEN 7\n" +
            "                             WHEN elecp.orgNo = 32101 THEN 8\n" +
            "                             WHEN elecp.orgNo = 33101 THEN 9\n" +
            "                             WHEN elecp.orgNo = 34101 THEN 10\n" +
            "                             WHEN elecp.orgNo = 35101 THEN 11\n" +
            "                             WHEN elecp.orgNo = 42102 THEN 12\n" +
            "                             WHEN elecp.orgNo = 43101 THEN 13\n" +
            "                             WHEN elecp.orgNo = 41101 THEN 14\n" +
            "                             WHEN elecp.orgNo = 36101 THEN 15\n" +
            "                             WHEN elecp.orgNo = 51101 THEN 16\n" +
            "                             WHEN elecp.orgNo = 50101 THEN 17\n" +
            "                             WHEN elecp.orgNo = 21102 THEN 18\n" +
            "                             WHEN elecp.orgNo = 22101 THEN 19\n" +
            "                             WHEN elecp.orgNo = 23101 THEN 20\n" +
            "                             WHEN elecp.orgNo = 15101 THEN 21\n" +
            "                             WHEN elecp.orgNo = 61102 THEN 22\n" +
            "                             WHEN elecp.orgNo = 62101 THEN 23\n" +
            "                             WHEN elecp.orgNo = 63101 THEN 24\n" +
            "                             WHEN elecp.orgNo = 64101 THEN 25\n" +
            "                             WHEN elecp.orgNo = 65101 THEN 26 \n" +
            "                     END AS serialNum\n" +
            "                    ,elecp.orgNo\n" +
            "                    ,elecp.orgName\n" +
            "                    ,REPLACE(FORMAT(power.purPrePower, 2), ',', '') AS purPrePower\n" +
            "                    ,REPLACE(FORMAT(elecp.sumqty, 2), ',', '') AS sumqty\n" +
            "                    ,REPLACE(FORMAT(round((1-abs(elecp.sumqty - power.purPrePower)/NULLIF(greatest(elecp.sumqty, power.purPrePower),0)) * 100,2),2),',','') AS preAccuracyBattery\n" +
            "                    ,REPLACE(FORMAT(power.preAverPrice, 2), ',', '') AS preAverPrice\n" +
            "                    ,REPLACE(FORMAT(elecp.avgprice, 2), ',', '') AS avgprice\n" +
            "                    ,REPLACE(FORMAT(round((1-abs(elecp.avgprice - power.preAverPrice)/NULLIF(greatest(elecp.avgprice, power.preAverPrice),0)) * 100,2),2),',','') AS preAccuracyPrice\n" +
            "            FROM    (\n" +
            "                        SELECT  aa.org_no AS orgNo\n" +
            "                                ,aa.org_name AS orgName\n" +
            "                                ,sum(aa.qty) AS sumqty\n" +
            "                                ,sum(aa.qty * nvl(bb.value, 0))/NULLIF(sum(aa.qty),0) AS avgprice\n" +
            "                        FROM    (\n" +
            "                                    SELECT  org_no\n" +
            "                                            ,CASE    WHEN org_no = '11102' THEN '北京'\n" +
            "                                                     WHEN org_no = '12101' THEN '天津'\n" +
            "                                                     WHEN org_no = '13102' THEN '河北'\n" +
            "                                                     WHEN org_no = '16101' THEN '冀北'\n" +
            "                                                     WHEN org_no = '14101' THEN '山西'\n" +
            "                                                     WHEN org_no = '37101' THEN '山东'\n" +
            "                                                     WHEN org_no = '31102' THEN '上海'\n" +
            "                                                     WHEN org_no = '32101' THEN '江苏'\n" +
            "                                                     WHEN org_no = '33101' THEN '浙江'\n" +
            "                                                     WHEN org_no = '34101' THEN '安徽'\n" +
            "                                                     WHEN org_no = '35101' THEN '福建'\n" +
            "                                                     WHEN org_no = '42102' THEN '湖北'\n" +
            "                                                     WHEN org_no = '43101' THEN '湖南'\n" +
            "                                                     WHEN org_no = '41101' THEN '河南'\n" +
            "                                                     WHEN org_no = '36101' THEN '江西'\n" +
            "                                                     WHEN org_no = '51101' THEN '四川'\n" +
            "                                                     WHEN org_no = '50101' THEN '重庆'\n" +
            "                                                     WHEN org_no = '21102' THEN '辽宁'\n" +
            "                                                     WHEN org_no = '22101' THEN '吉林'\n" +
            "                                                     WHEN org_no = '23101' THEN '龙江'\n" +
            "                                                     WHEN org_no = '15101' THEN '蒙东'\n" +
            "                                                     WHEN org_no = '61102' THEN '陕西'\n" +
            "                                                     WHEN org_no = '62101' THEN '甘肃'\n" +
            "                                                     WHEN org_no = '63101' THEN '青海'\n" +
            "                                                     WHEN org_no = '64101' THEN '宁夏'\n" +
            "                                                     WHEN org_no = '65101' THEN '新疆'\n" +
            "                                                     WHEN org_no = '65101' THEN '西藏' \n" +
            "                                             END AS org_name\n" +
            "                                            ,rpt_datetime\n" +
            "                                            ,date_format(rpt_date, '%%Y%%m') AS rpt_date\n" +
            "                                            ,item_title2\n" +
            "                                            ,value * 0.25 AS qty\n" +
            "                                            ,substr(item_title2, 1, 4) AS item_title\n" +
            "                                    FROM    ads_rpt_elecp_anal_his_df\n" +
            "                                    WHERE   serial_num IN ('3', '7')\n" +
            "                                    AND     date_format(rpt_date, '%%Y%%m') BETWEEN '%s' AND '%s'\n" +
            "                                    AND     org_no = '%s'\n" +
            "                                    AND     rpt_date NOT IN (%s)\n" +
            "                                ) aa\n" +
            "                        LEFT JOIN (\n" +
            "                                      SELECT  org_no\n" +
            "                                              ,org_name\n" +
            "                                              ,rpt_datetime\n" +
            "                                              ,date_format(rpt_date, '%%Y%%m') AS rpt_date\n" +
            "                                              ,item_title2\n" +
            "                                              ,value\n" +
            "                                              ,substr(item_title2, 1, 4) AS item_title\n" +
            "                                      FROM    ads_rpt_elecp_anal_his_df\n" +
            "                                      WHERE   serial_num IN('4', '8')\n" +
            "                                      AND     date_format(rpt_date, '%%Y%%m') BETWEEN '%s' AND '%s'\n" +
            "                                      AND     org_no = '%s'\n" +
            "                                      AND     rpt_date NOT IN (%s)\n" +
            "                                  ) bb\n" +
            "                        ON      aa.org_no = bb.org_no\n" +
            "                        AND     aa.rpt_date = bb.rpt_date\n" +
            "                        AND     aa.rpt_datetime = bb.rpt_datetime\n" +
            "                        AND     aa.item_title = bb.item_title\n" +
            "                        GROUP BY aa.org_no\n" +
            "                                 ,aa.org_name\n" +
            "                    ) elecp\n" +
            "            JOIN    (\n" +
            "                        SELECT  dept_code\n" +
            "                                ,dept_id\n" +
            "                                ,CASE    WHEN dept_code IN (11102, 12101, 13102, 16101, 14101, 37101) THEN '华北'\n" +
            "                                         WHEN dept_code IN (31102, 32101, 33101, 34101, 35101) THEN '华东'\n" +
            "                                         WHEN dept_code IN (42102, 43101, 41101, 36101) THEN '华中'\n" +
            "                                         WHEN dept_code IN (51101, 50101) THEN '西南'\n" +
            "                                         WHEN dept_code IN (61102, 62101, 63101, 64101, 65101) THEN '西北'\n" +
            "                                         WHEN dept_code IN (21102, 22101, 23101, 15101) THEN '东北' \n" +
            "                                         ELSE '其它' \n" +
            "                                 END AS region\n" +
            "                                ,nmbg_elec * 100000 AS gapPreOfElec\n" +
            "                                ,SUM(expect_elec * 100000) AS purPrePower\n" +
            "                                ,SUM((EXPECT_ELEC * 100000)* AVERAGE_ELEC)/NULLIF(SUM(EXPECT_ELEC * 100000),0) AS preAverPrice\n" +
            "                                ,extim_elec AS amortForDiscount\n" +
            "                        FROM    ads_cst_rpt_purch_power_anal_mf\n" +
            "                        WHERE   rpt_month BETWEEN '%s' AND '%s'\n" +
            "                        AND     dept_code = '%s'  \n" +
            "                        GROUP BY dept_code\n" +
            "                                 ,dept_id\n" +
            "                    ) power\n" +
            "            ON      elecp.orgNo = power.dept_code\n" +
            "            WHERE   elecp.orgNo <> '54101'\n" +
            "        ) tt;";

    public static final String QTY_PRICE_MONITOR_GW = "insert into ads_rpt_elec_qty_price_monitor_statistic(region,sort_num,org_no,org_name,stat_date,ddeal_price,serial_num37,qty_pre_accu_rto,dlgd_price,serial_num48,prc_pre_accu_rto,serial_number,create_time,isc)\n" +
            "SELECT  tt.region\n" +
            "        ,tt.serialNum sort_num\n" +
            "        ,tt.orgNo org_no\n" +
            "        ,tt.orgName org_name\n" +
            "        ,'%s' stat_date\n" +
            "        ,round(CAST(nvl(tt.purPrePower, 0) AS DECIMAL(18, 2)), 2) ddeal_price\n" +
            "        ,round(CAST(nvl(tt.sumqty, 0) AS DECIMAL(18, 2)), 2) serial_num37\n" +
            "        ,round(CAST(nvl(tt.preAccuracyBattery, 0) AS DECIMAL(18, 2)), 2) qty_pre_accu_rto\n" +
            "        ,round(CAST(nvl(tt.preAverPrice, 0) AS DECIMAL(18, 2)), 2) dlgd_price\n" +
            "        ,round(CAST(nvl(nullif(tt.avgprice, 0), 0) AS DECIMAL(18, 2)), 2) serial_num48\n" +
            "        ,round(CAST(nvl(nullif(tt.preAccuracyPrice, 0), 0) AS DECIMAL(18, 2)),2) prc_pre_accu_rto\n" +
            "        ,'%s' AS serial_number\n" +
            "        ,'%s' AS create_time\n" +
            "        ,'%s' AS isc\n" +
            "FROM    (\n" +
            "            SELECT  '国网' region\n" +
            "                    ,0 serialNum\n" +
            "                    ,elecp.orgNo\n" +
            "                    ,elecp.orgName\n" +
            "                    ,REPLACE(FORMAT(power.purPrePower,2),',','') AS purPrePower\n" +
            "                    ,REPLACE(FORMAT(elecp.sumqty,2),',','') AS sumqty\n" +
            "                    ,REPLACE(FORMAT(round((1-abs(elecp.sumqty - power.purPrePower)/NULLIF(greatest(elecp.sumqty,power.purPrePower),0)) * 100,2),2),',','') AS preAccuracyBattery\n" +
            "                    ,REPLACE(FORMAT(power.preAverPrice,2),',','') AS preAverPrice\n" +
            "                    ,REPLACE(FORMAT(elecp.avgprice,2),',','') AS avgprice\n" +
            "                    ,REPLACE(FORMAT(round((1-abs(elecp.avgprice - power.preAverPrice)/NULLIF(greatest(elecp.avgprice,power.preAverPrice),0)) * 100,2),2),',','') AS preAccuracyPrice\n" +
            "            FROM    (\n" +
            "                        SELECT  aa.orgNo\n" +
            "                                ,aa.orgName\n" +
            "                                ,sum(aa.qty) AS sumqty\n" +
            "                                ,sum(aa.qty*nvl(bb.value,0))/NULLIF(sum(aa.qty),0) AS avgprice\n" +
            "                        FROM    (\n" +
            "                                    SELECT  '11101' AS orgNo\n" +
            "                                            ,'国网' AS orgName\n" +
            "                                            ,org_no\n" +
            "                                            ,rpt_datetime\n" +
            "                                            ,date_format(rpt_date,'%%Y%%m') AS rpt_date\n" +
            "                                            ,item_title2\n" +
            "                                            ,value * 0.25 AS qty\n" +
            "                                            ,substr(item_title2, 1, 4) AS item_title\n" +
            "                                    FROM    pro_ads_cms2.ads_rpt_elecp_anal_his_df\n" +
            "                                    WHERE   serial_num IN('3','7')\n" +
            "                                    AND     date_format(rpt_date, '%%Y%%m') BETWEEN '%s' AND '%s' and %s\n" +
            "                                ) aa\n" +
            "                        LEFT JOIN (\n" +
            "                                      SELECT  '11101' AS orgNo\n" +
            "                                              ,'国网' AS orgName\n" +
            "                                              ,org_no\n" +
            "                                              ,rpt_datetime\n" +
            "                                              ,date_format(rpt_date,'%%Y%%m') AS rpt_date\n" +
            "                                              ,item_title2\n" +
            "                                              ,value\n" +
            "                                              ,substr(item_title2, 1, 4) AS item_title\n" +
            "                                      FROM    pro_ads_cms2.ads_rpt_elecp_anal_his_df\n" +
            "                                      WHERE   serial_num IN('4','8')\n" +
            "                                      AND     date_format(rpt_date, '%%Y%%m') BETWEEN '%s' AND '%s' and %s\n" +
            "                                  ) bb\n" +
            "                        ON      aa.org_no = bb.org_no\n" +
            "                        AND     aa.rpt_date = bb.rpt_date\n" +
            "                        AND     aa.rpt_datetime = bb.rpt_datetime\n" +
            "                        AND     aa.item_title = bb.item_title\n" +
            "                        GROUP BY aa.orgNo\n" +
            "                                 ,aa.orgName\n" +
            "                    ) elecp\n" +
            "            JOIN    (\n" +
            "                        SELECT  '11101' AS dept_code\n" +
            "                                ,sum(expect_elec * 100000) AS purPrePower\n" +
            "                                ,sum(average_elec * (expect_elec * 100000))/NULLIF(sum(expect_elec * 100000),0) AS preAverPrice\n" +
            "                        FROM    pro_ads_cms2.ads_cst_rpt_purch_power_anal_mf\n" +
            "                        WHERE   rpt_month BETWEEN '%s' AND '%s'\n" +
            "                        AND     dept_code in (%s)\n" +
            "                    ) power\n" +
            "            ON      elecp.orgNo = power.dept_code\n" +
            "        ) tt;";


    public static final String WBQL_PRO ="/sqlTemplate/ads_cst_wbql_emss_exam_index_result.properties";

    public static final String DW_PRO ="/sqlTemplate/py_content.properties";

    public static final String FOLDERPATH = "业务流程/dayi2.0check/数据开发/jicha_rule/";

    //prod
    public static final Long PROJECTID = 10022L;
    //build
    public static final Long BUILDIDPROJECTID = 600730L;

    public static final String PROJECT_NAME = "Pro_ADS_CMS2_root";

    public static final String PROERTIES = "/szyypt_servers.properties";


    public static final String ZT_3 = "with work_order_data as\n" +
            " (select WK_ORDER_HIS.provincial_id, --省单位编号\n" +
            "         WK_ORDER_HIS.mgt_Org_Code, --供电单位编号\n" +
            "         WK_ORDER_HIS.wk_order_no, -- 业扩工单编号\n" +
            "         WK_ORDER_HIS.wk_Order_Stat,\n" +
            "         WK_ORDER_HIS.creat_Time,\n" +
            "         WK_ORDER_HIS.acmp_Time,\n" +
            "         WK_ORDER_HIS.wk_Order_ID\n" +
            "    from pro_dwd_cms2_prod.UN94_02_CMS20_WK_ORDER_HIS WK_ORDER_HIS\n" +
            "   where WK_ORDER_HIS.ds = '${bizdate_ymd}'\n" +
            "     and WK_ORDER_HIS.wk_Order_Stat = '05'\n" +
            "     and WK_ORDER_HIS.acmp_Time >= DATEADD(TO_DATE(CONCAT('${bizdate1}', '26'), 'yyyymmdd'), -1, 'MM')\n" +
            "     and WK_ORDER_HIS.acmp_Time < TO_DATE(CONCAT('${bizdate1}', '26'), 'yyyymmdd')\n" +
            "     ),\n" +
            "cons_data as\n" +
            "( \n" +
            "select distinct \n" +
            "     t1.provincial_id     --省单位编号\n" +
            "    ,t1.mgt_Org_Code      --供电单位编号\n" +
            "    ,t3.wk_order_no       --业扩工单编号\n" +
            "    ,t3.wk_Order_Stat     --工单状态\n" +
            "    ,t1.bus_type          --业务类型\n" +
            "    ,t2.ec_Categ          --用电类别\n" +
            "    ,t2.voltage           --电压等级\n" +
            "    ,t4.PS_NUM            --电源数目\n" +
            "    ,t2.CUST_NO           --客户编号\n" +
            "    ,t2.cust_name         --客户名称\n" +
            "    ,t2.EC_ADDR           --用能地址\n" +
            "    ,t2.APP_CTRT_CAP      --申请合同容量\n" +
            "    ,t2.CTRT_CAP          --合计合同容量\n" +
            "    ,t5.HIGH_STD_FARM_LAND_FLAG --高标准农田标志\n" +
            "    ,t6.APRV_DATE         --审核日期\n" +
            "    ,t3.creat_Time        --工单创建时间\n" +
            "    ,t3.acmp_Time         --工单完成时间\n" +
            "    ,t3.wk_Order_ID\n" +
            "    ,t1.bus_App_Form_ID\n" +
            "    ,t2.cust_App_Rec_ID\n" +
            "    ,t2.CUST_ID\n" +
            "from (select \n" +
            "     provincial_id\n" +
            "\t,app_no\n" +
            "\t,bus_App_Form_ID\n" +
            "\t,mgt_Org_Code\n" +
            "\t,bus_type\n" +
            "from pro_dwd_cms2_prod.UN94_02_CMS20_BUS_APP_FORM\n" +
            "where ds = '${bizdate_ymd}'\n" +
            "and bus_type in ('010101', '010102') \n" +
            "        -- 010101  高压新装\n" +
            "        -- 010102  高压增容\n" +
            ") t1\n" +
            "join \n" +
            "(select \n" +
            "     provincial_id\n" +
            "    ,bus_App_Form_ID\n" +
            "\t,ec_Categ     --用电类别\n" +
            "    ,voltage      --电压等级\n" +
            "\t,CUST_NO      --客户编号\n" +
            "    ,cust_name    --客户名称\n" +
            "    ,EC_ADDR      --用能地址\n" +
            "    ,APP_CTRT_CAP --申请合同容量\n" +
            "    ,CTRT_CAP     --合计合同容量\n" +
            "\t,cust_App_Rec_ID\n" +
            "    ,CUST_ID\n" +
            "from pro_dwd_cms2_prod.UN94_02_CMS20_CUST_ELEC_APP_REC\n" +
            "where ds = '${bizdate_ymd}'\n" +
            ") t2\n" +
            "on t1.provincial_id = t2.provincial_id\n" +
            "and t1.bus_App_Form_ID = t2.bus_App_Form_ID\n" +
            "join \n" +
            "(select \n" +
            "     provincial_id --省单位编号\n" +
            "    ,mgt_Org_Code  --供电单位编号\n" +
            "    ,wk_order_no   --业扩工单编号\n" +
            "    ,wk_Order_Stat\n" +
            "    ,creat_Time\n" +
            "    ,acmp_Time\n" +
            "    ,wk_Order_ID\n" +
            "from work_order_data\n" +
            ") t3\n" +
            "on t1.provincial_id = t3.provincial_id\n" +
            "and t1.app_no = t3.wk_order_no\n" +
            "left join \n" +
            "(select \n" +
            "     provincial_id\n" +
            "    ,app_no\n" +
            "\t,count(distinct conn_No) as PS_NUM\n" +
            "   from pro_dwd_cms2_prod.un94_02_cms20_CONNECTION_APP_REC\n" +
            "  where ds = '${bizdate_ymd}'\n" +
            "    and ARCH_STATUS <> '04'\n" +
            "\tgroup by provincial_id\n" +
            "    ,app_no\n" +
            ") t4\n" +
            "on t1.provincial_id = t4.provincial_id\n" +
            "and t1.app_no = t4.app_No\n" +
            "left join \n" +
            "(select \n" +
            "     provincial_id\n" +
            "\t,app_no\n" +
            "\t,max(HIGH_STD_FARM_LAND_FLAG) as HIGH_STD_FARM_LAND_FLAG \n" +
            "   from pro_dwd_cms2_prod.dwd_cst_cust_irri_dra_fac_app\n" +
            "  where ds = '${bizdate_ymd}'\n" +
            "    group by provincial_id\n" +
            "\t,app_no\n" +
            ") t5\n" +
            "on t1.provincial_id = t5.provincial_id\n" +
            "and t1.app_no = t5.app_No\n" +
            "left join \n" +
            "(select \n" +
            "     provincial_id\n" +
            "\t,app_no\n" +
            "\t,max(APRV_DATE) as APRV_DATE  --审核日期\n" +
            "from pro_dwd_cms2_prod.un94_02_cms20_WKORDER_AFFILMTRL_REC\n" +
            "where ds = '${bizdate_ymd}'\n" +
            "--and PROJ_CATEG = '01'   --工程是否限制待定\n" +
            "and wk_order_affil_mtrl_rec_cls = '04'\n" +
            "group by provincial_id\n" +
            "\t,app_no\n" +
            ") t6\n" +
            "on t1.provincial_id = t6.provincial_id\n" +
            "and t1.app_no = t6.app_No\n" +
            "),\n" +
            "proc_instance as\n" +
            "(select distinct '1' as onece\n" +
            "                 ,a.provincial_id   --省单位编号\n" +
            "                 ,a.mgt_Org_Code    --供电单位编号\n" +
            "                 ,a.wk_order_no     --业扩工单编号\n" +
            "                 ,a.wk_Order_Stat   --工单状态\n" +
            "                 ,a.bus_type        --业务类型\n" +
            "                 ,a.ec_Categ        --用电类别\n" +
            "                 ,a.voltage         --电压等级\n" +
            "                 ,a.PS_NUM          --电源数量\n" +
            "                 ,a.CUST_NO         --客户编号\n" +
            "                 ,a.cust_name       --客户名称\n" +
            "                 ,a.EC_ADDR         --用能地址\n" +
            "                 ,a.APP_CTRT_CAP    --申请合同容量\n" +
            "                 ,a.CTRT_CAP        --合计合同容量\n" +
            "                 ,a.HIGH_STD_FARM_LAND_FLAG --高标准农田标志\n" +
            "                 ,a.APRV_DATE       --审核日期\n" +
            "                 ,a.creat_Time      --工单创建时间\n" +
            "                 ,a.acmp_Time       --工单完成时间\n" +
            "                 ,t1.proc_Acct_ID\n" +
            "                 ,A.wk_Order_ID\n" +
            "                 ,a.bus_App_Form_ID\n" +
            "                 ,a.cust_App_Rec_ID\n" +
            "                 ,a.CUST_ID \n" +
            "    from cons_data a\n" +
            "    join \n" +
            "\t(select \n" +
            "\t     provincial_id\n" +
            "\t\t,wk_Order_ID\n" +
            "\t\t,proc_Acct_ID\n" +
            "\tfrom pro_dwd_cms2_prod.UN94_02_CMS20_PROC_ACCT_HIS\n" +
            "\twhere ds = '${bizdate_ymd}'\n" +
            "\t) t1\n" +
            "\ton a.provincial_id = t1.provincial_id\n" +
            "    and a.wk_Order_ID = t1.wk_Order_ID\n" +
            "),\n" +
            "design_step as(\n" +
            "          select \n" +
            "                 STEP_REC_his.provincial_id\n" +
            "                ,STEP_REC_his.proc_Acct_ID\n" +
            "                ,STEP_REC_his.step_Acct_ID\n" +
            "                ,STEP_REC_his.step_Stat\n" +
            "                ,STEP_REC_his.step_No\n" +
            "                ,STEP_REC_his.step_Name\n" +
            "                ,STEP_REC_his.rcv_Time\n" +
            "                ,STEP_REC_his.acmp_Time\n" +
            "\t\t\t\t,row_number() over(partition by STEP_REC_his.provincial_id,STEP_REC_his.proc_Acct_ID order by STEP_REC_his.acmp_Time desc) as rn\n" +
            "            from pro_dwd_cms2_prod.UN94_02_CMS20_STEP_REC_his STEP_REC_his\n" +
            "           where EXISTS (select 1 from proc_instance a\n" +
            "                                 where a.provincial_id = STEP_REC_his.provincial_id\n" +
            "                                   and a.proc_Acct_ID = STEP_REC_his.proc_Acct_ID)\n" +
            "                                   and STEP_REC_his.step_No in ('BAC_0100133') --设计文件审核\n" +
            "                                   and STEP_REC_his.ds  = '${bizdate_ymd}'\n" +
            "),\n" +
            " base_data as(\n" +
            "          select  '1' as onece\n" +
            "                  ,a.provincial_id             --省单位编号\n" +
            "                  ,a.mgt_Org_Code              --供电单位编号\n" +
            "                  ,a.wk_order_no               --业扩工单编号\n" +
            "                  ,a.wk_Order_Stat             --工单状态\n" +
            "                  ,a.bus_type                  --业务类型\n" +
            "                  ,a.ec_Categ                  --用电类别\n" +
            "                  ,a.voltage                   --电压等级\n" +
            "                  ,a.PS_NUM                    --电源数量\n" +
            "                  ,a.CUST_NO                   --客户编号\n" +
            "                  ,a.cust_name                 --客户名称\n" +
            "                  ,a.EC_ADDR                   --用能地址\n" +
            "                  ,a.APP_CTRT_CAP              --申请合同容量\n" +
            "                  ,a.CTRT_CAP                  --合计合同容量\n" +
            "                  ,a.HIGH_STD_FARM_LAND_FLAG   --高标准农田标志\n" +
            "                  ,a.APRV_DATE                 --审核日期\n" +
            "                  ,design_step.acmp_Time as design_acmp_time --设计文件报审完成时间\n" +
            "                  ,CASE WHEN a.APRV_DATE > design_step.acmp_Time THEN  \n" +
            "                    DATEDIFF(a.APRV_DATE,design_step.acmp_Time,'dd')\n" +
            "                       WHEN design_step.acmp_Time > a.APRV_DATE THEN    \n" +
            "                    DATEDIFF(design_step.acmp_Time,a.APRV_DATE,'dd') - (select count(distinct holiday_date)\n" +
            "                        from (select '1' AS onece, holiday_date\n" +
            "                                from ads_cst_yx2_holiday_date_yf) hld\n" +
            "                       where hld.holiday_date >=\n" +
            "                             to_char(a.APRV_DATE, 'yyyymmdd')\n" +
            "                         and hld.holiday_date <=\n" +
            "                             to_char(design_step.acmp_Time, 'yyyymmdd')\n" +
            "                         and hld.onece = a.onece)    \n" +
            "                  END as diff_work_day        --差值（工作日）\n" +
            "                  ,CASE WHEN a.APRV_DATE > design_step.acmp_Time THEN  \n" +
            "                    DATEDIFF(a.APRV_DATE,design_step.acmp_Time,'dd') \n" +
            "                       WHEN design_step.acmp_Time > a.APRV_DATE THEN    \n" +
            "                    DATEDIFF(design_step.acmp_Time,a.APRV_DATE,'dd') \n" +
            "                  END as diff_day             --差值（自然日）\n" +
            "                  ,a.creat_Time               --工单创建时间\n" +
            "                  ,a.acmp_Time                --工单完成时间\n" +
            "                  ,A.wk_Order_ID\n" +
            "                  ,a.bus_App_Form_ID\n" +
            "                  ,a.cust_App_Rec_ID\n" +
            "                  ,a.CUST_ID \n" +
            "        from proc_instance a\n" +
            "        join design_step\n" +
            "          on a.provincial_id = design_step.provincial_id     \n" +
            "         and a.proc_Acct_ID = design_step.proc_Acct_ID\n" +
            "\t\t and design_step.rn = 1\n" +
            "       where DATEDIFF(a.APRV_DATE,design_step.acmp_Time,'dd') >= 1\n" +
            "          or DATEDIFF(design_step.acmp_Time,a.APRV_DATE,'dd') - (select count(distinct holiday_date)\n" +
            "                        from (select '1' AS onece, holiday_date\n" +
            "                                from ads_cst_yx2_holiday_date_yf) hld\n" +
            "                       where hld.holiday_date >=\n" +
            "                             to_char(a.APRV_DATE, 'yyyymmdd')\n" +
            "                         and hld.holiday_date <=\n" +
            "                             to_char(design_step.acmp_Time, 'yyyymmdd')\n" +
            "                         and hld.onece = a.onece) >= 3\n" +
            "            )   \n" +
            " \n" +
            "\n" +
            "INSERT OVERWRITE TABLE ADS_CST_YXJCZX_SPCL_SCREEN_DATA_DET PARTITION(ds,subj_no_sub)\n" +
            "select \n" +
            "        concat('11101',substr('${bizdate1}',3,4),'000093',100000000+ROW_NUMBER() OVER()) as DATA_ID\n" +
            "        ,'JCZX_000093'           AS SCREEN_TASK_NO \n" +
            "        ,case when t8.provincial_id = '10500001' then '11102'                           --北京\n" +
            "              when t8.provincial_id = '10000028' then '12101'                           --天津\n" +
            "              when t8.provincial_id = '10372269' then '13102'                           --河北\n" +
            "              when t8.provincial_id = '10687637' then '16101'                           --冀北\n" +
            "              when t8.provincial_id = '10583026' then '14101'                           --山西\n" +
            "              when t8.provincial_id = '10554019' then '37101'                           --山东 \n" +
            "              when t8.provincial_id = '10010340' then '31102'                           --上海\n" +
            "              when t8.provincial_id = '10430001' then '32101'                           --江苏\n" +
            "              when t8.provincial_id = '10262690' then '33101'                           --浙江\n" +
            "              when t8.provincial_id = '10330027' then '34101'                           --安徽\n" +
            "              when t8.provincial_id = '10220116' then '35101'                           --福建\n" +
            "              when t8.provincial_id = '10467828' then '42102'                           --湖北\n" +
            "              when t8.provincial_id = '10175327' then '43101'                           --湖南\n" +
            "              when t8.provincial_id = '10522001' then '41101'                           --河南\n" +
            "              when t8.provincial_id = '10090121' then '36101'                           --江西 \n" +
            "              when t8.provincial_id = '10300001' then '51101'                           --四川\n" +
            "              when t8.provincial_id = '10060001' then '50101'                           --重庆\n" +
            "              when t8.provincial_id = '10130001' then '21102'                           --辽宁 \n" +
            "              when t8.provincial_id = '10200000' then '22101'                           --吉林\n" +
            "              when t8.provincial_id = '10400000' then '23101'                           --龙江\n" +
            "              when t8.provincial_id = '10045073' then '15101'                           --蒙东\n" +
            "              when t8.provincial_id = '10750029' then '61102'                           --陕西\n" +
            "              when t8.provincial_id = '10778300' then '62101'                           --甘肃\n" +
            "              when t8.provincial_id = '10798565' then '63101'                           --青海 \n" +
            "              when t8.provincial_id = '10606629' then '64101'                           --宁夏\n" +
            "              when t8.provincial_id = '10621001' then '65101'                           --新疆\n" +
            "              when t8.provincial_id = '10520001' then '54101'                           --西藏 \n" +
            "         else t8.provincial_id \n" +
            "         end                                as PROV_ORG_NO               --省公司单位编码\n" +
            "        ,t8.mgt_Org_Code                    as PS_ORG_NO                 --供电单位\n" +
            "        ,tb3.city_org_no                    as FLD1                      --地市单位编码\n" +
            "        ,tb3.country_org_no                 as FLD2                      --区县单位编码\n" +
            "\t\t,t8.wk_order_no                  \tas FLD3                      --工单编号\n" +
            "        ,ccv1.code_Cls_Val_Name             as FLD4                      --工单状态\n" +
            "        ,ccv2.code_Cls_Val_Name      \t\tAS FLD5                      --业务类型\n" +
            "        ,ccv3.code_Cls_Val_Name      \t\tAS FLD6                      --用电类别大类\n" +
            "        ,ccv4.code_Cls_Val_Name      \t\tAS FLD7                      --用电类别\n" +
            "        ,t8.voltage_vk_name                 AS FLD8                      --电压等级大类\n" +
            "        ,ccv6.code_Cls_Val_Name      \t    AS FLD9                      --电压等级\n" +
            "        ,ccv7.code_Cls_Val_Name             AS FLD10                     --电源类型\n" +
            "        ,t8.CUST_NO                  \t \tAS FLD11                     --用户编号    \n" +
            "        ,t8.cust_name      \t\t\t \t    AS FLD12                     --用户名称\n" +
            "        ,t8.EC_ADDR               \t        AS FLD13                     --用电地址\n" +
            "        ,t8.APP_CTRT_CAP              \t    AS FLD14                     --申请合同容量\n" +
            "        ,t8.CTRT_CAP      \t\t\t\t    AS FLD15                     --合计合同容量\n" +
            "        ,ccv8.code_Cls_Val_Name      \t    AS FLD16                     --是否高标准农田\n" +
            "        ,t8.APRV_DATE              \t\t    AS FLD17                     --审核日期\n" +
            "\t\t,t8.design_acmp_time              \tAS FLD18                     --设计文件审核完成时间\n" +
            "\t\t,t8.diff_work_day               \tAS FLD19                     --差值（工作日）\n" +
            "        ,'${bizdate1}'           \t\t    AS FLD20                     --异常年月\n" +
            "        ,null                           \tas FLD21                     \n" +
            "        ,null                    \t        as FLD22                     \n" +
            "        ,null                    \t        as FLD23\t\t\t\t\t    \n" +
            "        ,null                               as FLD24\t\t\t\t\t  \n" +
            "        ,null                        \t    as FLD25\t\t\t\t\t  \n" +
            "        ,null                        \t    as FLD26\t\t\t\t\t  \n" +
            "        ,null                               as FLD27\n" +
            "        ,null                               as FLD28\n" +
            "        ,null                               as FLD29\n" +
            "        ,null                               as FLD30\n" +
            "        ,null                               as FLD31\n" +
            "        ,null                               as FLD32\n" +
            "        ,null                               as FLD33\n" +
            "        ,null                               as FLD34\n" +
            "        ,null                               as FLD35\n" +
            "        ,null                               as FLD36\n" +
            "        ,null                               as FLD37\n" +
            "        ,null                               as FLD38\n" +
            "        ,null                               as FLD39\n" +
            "        ,null                               as FLD40\n" +
            "        ,null                               as FLD41\n" +
            "        ,null                               as FLD42\n" +
            "        ,null                               as FLD43\n" +
            "        ,null                               as FLD44\n" +
            "        ,null                               as FLD45\n" +
            "        ,null                               as FLD46\n" +
            "        ,null                               as FLD47\n" +
            "        ,null                               as FLD48\n" +
            "        ,null                               as FLD49\n" +
            "        ,null                               as FLD50\n" +
            "\t\t,NULL                               AS FLD51\n" +
            "        ,NULL                               AS FLD52\n" +
            "        ,NULL                               AS FLD53\n" +
            "        ,NULL                               AS FLD54\n" +
            "        ,NULL                               AS FLD55\n" +
            "        ,NULL                               AS FLD56\n" +
            "        ,NULL                               AS FLD57\n" +
            "        ,NULL                               AS FLD58\n" +
            "        ,NULL                               AS FLD59\n" +
            "\t\t,NULL                               AS FLD60\n" +
            "        ,GETDATE() AS TIME_POKE\n" +
            "        ,'${bizdate1}'\n" +
            "        ,'JCZX_000093'\n" +
            "FROM    (\n" +
            " select  a.provincial_id   --省单位编号\n" +
            "        ,a.mgt_Org_Code    --供电单位编号\n" +
            "        ,a.wk_order_no     --业扩工单编号\n" +
            "        ,a.wk_Order_Stat   --工单状态\n" +
            "        ,a.bus_type        --业务类型\n" +
            "        ,substr(a.ec_categ,1,2) as ec_categ_vk --用电类别_大类\n" +
            "        ,a.ec_Categ        --用电类别\n" +
            "        ,case when trim(a.voltage) in ('0115','0116','0117','0118','0119') then '01'      when trim(a.voltage) in ('0120','0121','0142') then '02'\n" +
            "              when trim(a.voltage) in ('0122','0123','0124','0125','0126','0127','0128') then '03' \n" +
            "              else voltage \n" +
            "              end as voltage_vk --电压等级大类\n" +
            "\t\t,case when trim(a.voltage) in ('0115','0116','0117','0118','0119') then '10（20）千伏'      \n" +
            "\t\t      when trim(a.voltage) in ('0120','0121','0142') then '35千伏'\n" +
            "              when trim(a.voltage) in ('0122','0123','0124','0125','0126','0127','0128') then '110（66）千伏及以上' \n" +
            "              else voltage \n" +
            "              end as voltage_vk_name --电压等级大类--voltLvYK\t  \n" +
            "        ,a.voltage              --电压等级\n" +
            "        ,case when a.ps_num > 2 then '03'     --电源数目  多电源优先多电源\n" +
            "              when a.ps_num = 2 then '01'     --其次双电源\n" +
            "              when a.ps_num = 1 then '02'     --最后单电源\n" +
            "              else a.ps_num\n" +
            "              end as PS_NUM        --电源类型(电源数量)\n" +
            "        ,a.CUST_NO                 --用户编号\n" +
            "        ,a.cust_name               --用户名称\n" +
            "        ,a.EC_ADDR                 --用电地址\n" +
            "        ,a.APP_CTRT_CAP            --申请合同容量\n" +
            "        ,a.CTRT_CAP                --合计合同容量\n" +
            "        ,a.HIGH_STD_FARM_LAND_FLAG --是否高标准农田\n" +
            "        ,a.APRV_DATE               --审核日期\n" +
            "        ,a.design_acmp_time        --设计文件报审完成时间\n" +
            "        ,a.diff_work_day           --差值_工作日\n" +
            "        ,a.diff_day                --差值_自然日\n" +
            "        ,a.creat_Time\n" +
            "        ,a.acmp_Time\n" +
            "        ,A.wk_Order_ID\n" +
            "        ,a.bus_App_Form_ID\n" +
            "        ,a.cust_App_Rec_ID\n" +
            "        ,a.CUST_ID \n" +
            " from base_data a\n" +
            ")t8\n" +
            "left join \n" +
            "(\n" +
            "    SELECT\n" +
            "        mgt_org_code,\n" +
            "        country_org_no,\n" +
            "        city_org_no,\n" +
            "        prov_org_no,\n" +
            "        ds\n" +
            "    FROM ads_cst_yx2_yxjc_mgt_org_2_df\n" +
            "    WHERE ds = '${bizdate_ymd1}'\n" +
            ") tb3\n" +
            "on t8.MGT_ORG_CODE = tb3.MGT_ORG_CODE\n" +
            "left join \n" +
            "        (\n" +
            "            SELECT \n" +
            "                code_Cls_Type\n" +
            "                ,code_Cls_Val  --码值\n" +
            "                ,code_Cls_Val_Name  \n" +
            "                ,provincial_id\n" +
            "                ,ds\n" +
            "            FROM pro_dwd_cms2_prod.UN94_02_CMS20_Code_Cls_Val\n" +
            "            where ds = '${bizdate_ymd}'\n" +
            "            and valid_flag = '02'  --生效\n" +
            "            and code_Cls_Type = 'wkOrderStat'  --工单状态\n" +
            "        ) ccv1\n" +
            "on t8.provincial_id = ccv1.provincial_id\n" +
            "and t8.wk_Order_Stat = ccv1.code_Cls_Val\n" +
            "left join \n" +
            "        (\n" +
            "            SELECT \n" +
            "                code_Cls_Type\n" +
            "                ,code_Cls_Val  --码值\n" +
            "                ,code_Cls_Val_Name\n" +
            "                ,provincial_id\n" +
            "                ,ds\n" +
            "            FROM pro_dwd_cms2_prod.UN94_02_CMS20_Code_Cls_Val\n" +
            "            where ds = '${bizdate_ymd}'\n" +
            "            and valid_flag = '02'  --生效\n" +
            "            and code_Cls_Type = 'dmdType'  --业务类型\n" +
            "        ) ccv2\n" +
            "on t8.provincial_id = ccv2.provincial_id\n" +
            "and t8.bus_type = ccv2.code_Cls_Val\n" +
            "left join \n" +
            "        (\n" +
            "            SELECT \n" +
            "                code_Cls_Type\n" +
            "                ,code_Cls_Val  --码值\n" +
            "                ,code_Cls_Val_Name\n" +
            "                ,provincial_id\n" +
            "                ,ds\n" +
            "            FROM pro_dwd_cms2_prod.UN94_02_CMS20_Code_Cls_Val\n" +
            "            where ds = '${bizdate_ymd}'\n" +
            "            and valid_flag = '02'  --生效\n" +
            "            and code_Cls_Type = 'ecCateg'  --用电类别大类\n" +
            "        ) ccv3\n" +
            "on t8.provincial_id = ccv3.provincial_id\n" +
            "and t8.ec_categ_vk = ccv3.code_Cls_Val\n" +
            "left join \n" +
            "        (\n" +
            "            SELECT \n" +
            "                code_Cls_Type\n" +
            "                ,code_Cls_Val  --码值\n" +
            "                ,code_Cls_Val_Name\n" +
            "                ,provincial_id\n" +
            "                ,ds\n" +
            "            FROM pro_dwd_cms2_prod.UN94_02_CMS20_Code_Cls_Val\n" +
            "            where ds = '${bizdate_ymd}'\n" +
            "            and valid_flag = '02'  --生效\n" +
            "            and code_Cls_Type = 'ecCateg'  --用电类别\n" +
            "        ) ccv4\n" +
            "on t8.provincial_id = ccv4.provincial_id\n" +
            "and t8.ec_Categ = ccv4.code_Cls_Val\n" +
            "left join \n" +
            "        (\n" +
            "            SELECT \n" +
            "                code_Cls_Type\n" +
            "                ,code_Cls_Val  --码值\n" +
            "                ,code_Cls_Val_Name\n" +
            "                ,provincial_id\n" +
            "                ,ds\n" +
            "            FROM pro_dwd_cms2_prod.UN94_02_CMS20_Code_Cls_Val\n" +
            "            where ds = '${bizdate_ymd}'\n" +
            "            and valid_flag = '02'  --生效\n" +
            "            and code_Cls_Type = 'baseVolt'  --电压等级\n" +
            "        ) ccv6\n" +
            "on t8.provincial_id = ccv6.provincial_id\n" +
            "and t8.voltage = ccv6.code_Cls_Val\n" +
            "left join \n" +
            "        (\n" +
            "            SELECT \n" +
            "                code_Cls_Type\n" +
            "                ,code_Cls_Val  --码值\n" +
            "                ,code_Cls_Val_Name  \n" +
            "                ,provincial_id\n" +
            "                ,ds\n" +
            "            FROM pro_dwd_cms2_prod.UN94_02_CMS20_Code_Cls_Val\n" +
            "            where ds = '${bizdate_ymd}'\n" +
            "            and valid_flag = '02'  --生效\n" +
            "            and code_Cls_Type = 'psMode'  --电源数目\n" +
            "        ) ccv7\n" +
            "on t8.provincial_id = ccv7.provincial_id\n" +
            "and t8.PS_NUM = ccv7.code_Cls_Val\n" +
            "left join \n" +
            "        (\n" +
            "            SELECT \n" +
            "                code_Cls_Type\n" +
            "                ,code_Cls_Val  --码值\n" +
            "                ,code_Cls_Val_Name\n" +
            "                ,provincial_id\n" +
            "                ,ds\n" +
            "            FROM pro_dwd_cms2_prod.UN94_02_CMS20_Code_Cls_Val\n" +
            "            where ds = '${bizdate_ymd}'\n" +
            "            and valid_flag = '02'  --生效\n" +
            "            and code_Cls_Type = 'isFlag'  --是否高标准农田\n" +
            "        ) ccv8\n" +
            "on t8.provincial_id = ccv8.provincial_id\n" +
            "and t8.HIGH_STD_FARM_LAND_FLAG = ccv8.code_Cls_Val;";

    public static final String ZT_2 ="with cust_data as (\n" +
            "select \n" +
            " ecc.provincial_id\n" +
            "\t,ecc.mgt_org_code\n" +
            "\t,ecc.cust_no\n" +
            "\t,ecc.cust_name\n" +
            "\t,ecc.ind_cls   --行业分类\n" +
            "\t,ecc.ec_categ  --用电类别\n" +
            "\t,ecc.VOLTAGE   --电压等级\n" +
            "\t,ecc.CTRT_CAP  --合同容量\n" +
            "\t,ecc.RUN_CAP   --运行容量\n" +
            "\t,ecc.cust_id\n" +
            "\t,ecc.bp_id     --伙伴标识\n" +
            "from pro_dwd_cms2_prod.un94_02_cms20_elec_cons_cust ecc\n" +
            "where ds = '20260202'\n" +
            "and ECC_STAT <> '09'  --剔除已销户\n" +
            "and substr(EC_CATEG,1,2) <> '01'  --剔除考核\n" +
            "and exists (\n" +
            "--获取存在不为考核计量点或考核表的用户\n" +
            "select 1 from \n" +
            "    (select distinct\n" +
            "\t     t2.provincial_id\n" +
            "\t\t,t2.cust_ID\n" +
            "    from (\n" +
            "            select \n" +
            "                cust_ID\t\t\t--客户标识\n" +
            "                ,inst_ID\t\t\t--安装点标识\n" +
            "                ,provincial_id\t\t--网省分区字段\n" +
            "            from pro_dwd_cms2_prod.UN94_02_CMS20_INST_ELEC_CONS\t\t\t--计量点-用电(表)  浙江安徽\n" +
            "            where ds = '${bizdate_ymd}'\n" +
            "            and inst_Stat in ('01','02')\t\t\t--安装点状态为 02在用 01 设立\n" +
            "            and INST_USAGE_CLS not in ('18','19')       --18\t参考电量        19\t考核电量\n" +
            "            and INST_CHAR <> '02'           --02\t考核\n" +
            "        ) t2\n" +
            "\t    join \n" +
            "\t    (\n" +
            "            select \n" +
            "                INST_ID\n" +
            "                ,cust_ID\n" +
            "                ,REF_METER_FLAG --参考表标志\n" +
            "                ,provincial_id\n" +
            "            from pro_dwd_cms2_prod.un94_02_cms20_METER_RUN  --计量表计\n" +
            "            where ds = '${bizdate_ymd}' \n" +
            "            and REF_METER_FLAG = '01'           --01\t否\n" +
            "\t    ) t4\n" +
            "\t    on t2.cust_ID = t4.cust_ID\n" +
            "\t    and t2.inst_ID = t4.inst_ID\n" +
            "\t    and t2.provincial_id = t4.provincial_id\n" +
            "    ) t3\n" +
            "where ecc.provincial_id = t3.provincial_id\n" +
            "and ecc.cust_ID = t3.cust_id\n" +
            ")\n" +
            ")\n" +
            ",base_data_01 as (\n" +
            "select \n" +
            "     cust_data.provincial_id\n" +
            "\t,mgt_org_code\n" +
            "\t,cust_data.cust_no\n" +
            "\t,cust_name\n" +
            "\t,ind_cls   --行业分类\n" +
            "\t,ec_categ  --用电类别\n" +
            "\t,VOLTAGE   --电压等级\n" +
            "\t,CTRT_CAP  --合同容量\n" +
            "\t,RUN_CAP   --运行容量\n" +
            "\t,cust_id\n" +
            "\t,t1.CERT_NUMBER  --证件号码\n" +
            "from cust_data\n" +
            "join\n" +
            "(select \n" +
            "     provincial_id\n" +
            "\t,CERT_NUMBER\n" +
            "\t,cust_no\n" +
            "FROM pro_dwd_cms2_prod.un94_02_cms20_cert_ba_info  --证件基础信息\n" +
            "where ds = '${bizdate_ymd}'\n" +
            "and CERT_NUMBER <> '' \n" +
            "and CERT_NUMBER is not null\n" +
            ") t1\n" +
            "on t1.provincial_id = cust_data.provincial_id\n" +
            "and t1.cust_no = cust_data.cust_no\n" +
            ")\n" +
            ",error_01 as (\n" +
            "select \n" +
            "     provincial_id\n" +
            "\t,mgt_org_code\n" +
            "\t,cust_no\n" +
            "\t,cust_name\n" +
            "\t,ind_cls   --行业分类\n" +
            "\t,ec_categ  --用电类别\n" +
            "\t,VOLTAGE   --电压等级\n" +
            "\t,CTRT_CAP  --合同容量\n" +
            "\t,RUN_CAP   --运行容量\n" +
            "\t,cust_id\n" +
            "\t--,concat(substr(CERT_NUMBER,1,6),'********',substr(CERT_NUMBER,-4)) as pers_info --证件号码\n" +
            "\t,CERT_NUMBER as pers_info --证件号码\n" +
            "\t,'01' as error_type\n" +
            "from base_data_01\n" +
            "where exists \n" +
            "--存在同一证件号码超过50个不同用户名称的数据\n" +
            "(select 1\n" +
            "from (select \n" +
            "          provincial_id\n" +
            "         ,CERT_NUMBER\n" +
            "\t\t ,count(distinct cust_name) as cust_nums\n" +
            "      from base_data_01\n" +
            "\t  group by provincial_id\n" +
            "         ,CERT_NUMBER\n" +
            "\t\t having count(distinct cust_name) > 50\n" +
            "\t ) t1\n" +
            "where t1.provincial_id = base_data_01.provincial_id\n" +
            "and t1.CERT_NUMBER = base_data_01.CERT_NUMBER\n" +
            ")\n" +
            ")\n" +
            ",base_data_02 as (\n" +
            "--基础数据为用户维度，关联手机号会造成用户编号重复，原因为一个用户的伙伴会对应多个手机号，专项时，不需要去重，在线时，不展示手机号，需去重\n" +
            "select \n" +
            "     cust_data.provincial_id\n" +
            "\t,mgt_org_code\n" +
            "\t,cust_no\n" +
            "\t,cust_name\n" +
            "\t,ind_cls   --行业分类\n" +
            "\t,ec_categ  --用电类别\n" +
            "\t,VOLTAGE   --电压等级\n" +
            "\t,CTRT_CAP  --合同容量\n" +
            "\t,RUN_CAP   --运行容量\n" +
            "\t,cust_id\n" +
            "\t,t1.mobile --联系电话\n" +
            "from cust_data\n" +
            "join\n" +
            "(select provincial_id\n" +
            "\t,bp_id      --伙伴标识\n" +
            "    ,mobile\n" +
            "from (\n" +
            "    select provincial_id\n" +
            "    \t,bp_id      --伙伴标识\n" +
            "        ,concat(nvl(mobile_set_f,'未知'),',',nvl(mobile_set_s,'未知')) as mobile_set\n" +
            "    from (select \n" +
            "             provincial_id\n" +
            "        \t,bp_id      --伙伴标识\n" +
            "            ,concat_ws(',',collect_set(MOBILE_FRST)) as mobile_set_f\n" +
            "            ,concat_ws(',',collect_set(MOBILE_SCND)) as mobile_set_s\n" +
            "        from pro_dwd_cms2_prod.un94_02_cms20_BP_CONTACT\n" +
            "        where ds = '${bizdate_ymd}'\n" +
            "        group by provincial_id\n" +
            "        \t,bp_id      --伙伴标识\n" +
            "    )\n" +
            "    ) t\n" +
            "    lateral view explode(split(mobile_set,',')) t2 as mobile\n" +
            ") t1\n" +
            "on t1.provincial_id = cust_data.provincial_id\n" +
            "and t1.bp_id = cust_data.bp_id\n" +
            ")\n" +
            ",error_02 as (\n" +
            "select \n" +
            "     provincial_id\n" +
            "\t,mgt_org_code\n" +
            "\t,cust_no\n" +
            "\t,cust_name\n" +
            "\t,ind_cls   --行业分类\n" +
            "\t,ec_categ  --用电类别\n" +
            "\t,VOLTAGE   --电压等级\n" +
            "\t,CTRT_CAP  --合同容量\n" +
            "\t,RUN_CAP   --运行容量\n" +
            "\t,cust_id\n" +
            "\t--,concat(substr(mobile,1,3),'****',substr(mobile,-4)) as pers_info --联系电话\n" +
            "\t,mobile as pers_info --联系电话\n" +
            "\t,'02' as error_type\n" +
            "from base_data_02\n" +
            "where exists \n" +
            "--存在同一证件号码超过50个不同用户名称的数据\n" +
            "(select 1\n" +
            "from (select \n" +
            "          provincial_id\n" +
            "         ,mobile\n" +
            "\t\t ,count(distinct cust_name) as cust_nums\n" +
            "      from base_data_02\n" +
            "\t  group by provincial_id\n" +
            "         ,mobile\n" +
            "\t\t having count(distinct cust_name) > 50\n" +
            "\t ) t1\n" +
            "where t1.provincial_id = base_data_02.provincial_id\n" +
            "and t1.mobile = base_data_02.mobile\n" +
            ")\n" +
            ")\n" +
            "\n" +
            "INSERT OVERWRITE TABLE ADS_CST_YXJCZX_SPCL_SCREEN_DATA_DET PARTITION(ds,subj_no_sub)\n" +
            "select \n" +
            "        concat('11101',substr('${bizdate1}',3,4),'000098',100000000+ROW_NUMBER() OVER()) as DATA_ID\n" +
            "        ,'JCZX_000098'           AS SCREEN_TASK_NO \n" +
            "        ,case when t8.provincial_id = '10500001' then '11102'                           --北京\n" +
            "              when t8.provincial_id = '10000028' then '12101'                           --天津\n" +
            "              when t8.provincial_id = '10372269' then '13102'                           --河北\n" +
            "              when t8.provincial_id = '10687637' then '16101'                           --冀北\n" +
            "              when t8.provincial_id = '10583026' then '14101'                           --山西\n" +
            "              when t8.provincial_id = '10554019' then '37101'                           --山东 \n" +
            "              when t8.provincial_id = '10010340' then '31102'                           --上海\n" +
            "              when t8.provincial_id = '10430001' then '32101'                           --江苏\n" +
            "              when t8.provincial_id = '10262690' then '33101'                           --浙江\n" +
            "              when t8.provincial_id = '10330027' then '34101'                           --安徽\n" +
            "              when t8.provincial_id = '10220116' then '35101'                           --福建\n" +
            "              when t8.provincial_id = '10467828' then '42102'                           --湖北\n" +
            "              when t8.provincial_id = '10175327' then '43101'                           --湖南\n" +
            "              when t8.provincial_id = '10522001' then '41101'                           --河南\n" +
            "              when t8.provincial_id = '10090121' then '36101'                           --江西 \n" +
            "              when t8.provincial_id = '10300001' then '51101'                           --四川\n" +
            "              when t8.provincial_id = '10060001' then '50101'                           --重庆\n" +
            "              when t8.provincial_id = '10130001' then '21102'                           --辽宁 \n" +
            "              when t8.provincial_id = '10200000' then '22101'                           --吉林\n" +
            "              when t8.provincial_id = '10400000' then '23101'                           --龙江\n" +
            "              when t8.provincial_id = '10045073' then '15101'                           --蒙东\n" +
            "              when t8.provincial_id = '10750029' then '61102'                           --陕西\n" +
            "              when t8.provincial_id = '10778300' then '62101'                           --甘肃\n" +
            "              when t8.provincial_id = '10798565' then '63101'                           --青海 \n" +
            "              when t8.provincial_id = '10606629' then '64101'                           --宁夏\n" +
            "              when t8.provincial_id = '10621001' then '65101'                           --新疆\n" +
            "              when t8.provincial_id = '10520001' then '54101'                           --西藏 \n" +
            "         else t8.provincial_id \n" +
            "         end                                as PROV_ORG_NO               --省公司单位编码\n" +
            "        ,t8.mgt_Org_Code                    as PS_ORG_NO                 --供电单位\n" +
            "        ,tb3.city_org_no                    as FLD1                      --地市单位\n" +
            "        ,tb3.country_org_no                 as FLD2                      --区县单位\n" +
            "\t\t,cust_no                  \t        as FLD3                      --用户编号\n" +
            "        ,cust_name                          as FLD4                      --用户名称\n" +
            "        ,ccv1.code_Cls_Val_Name      \t\tAS FLD5                      --行业分类\n" +
            "        ,ccv2.code_Cls_Val_Name      \t\tAS FLD6                      --用电类别\n" +
            "        ,case when trim(t8.voltage) in ('0101','0102','0103','0104','0105','0106','0107','0108','0109','0110','0111','0112') then '01'\t--量价费电压等级大类\n" +
            "\t          when trim(t8.voltage) in ('0113','0114','0115','0116','0117') then '02'\n" +
            "\t          when trim(t8.voltage) in ('0118','0119','0120','0121','0142') then '03'\n" +
            "\t          when trim(t8.voltage) in ('0122','0123') then '04'\n" +
            "\t          when trim(t8.voltage) in ('0124') then '05'\n" +
            "\t          when trim(t8.voltage) in ('0125','0126','0127','0128') then '06' else null \n" +
            "\t\t\t  end      \t\tAS FLD7                      --电压等级大类\n" +
            "        ,ccv3.code_Cls_Val_Name             AS FLD8                      --电压等级\n" +
            "        ,t8.CTRT_CAP      \t                AS FLD9                      --合同容量\n" +
            "        ,t8.RUN_CAP                         AS FLD10                     --运行容量\n" +
            "        ,t8.pers_info                  \t \tAS FLD11                     --证件号码\n" +
            "        ,t8.error_type      \t\t\t \tAS FLD12                     --异常类型\n" +
            "        ,'${bizdate1}'              \t    AS FLD13                     --异常年月\n" +
            "        ,null              \t                AS FLD14\n" +
            "        ,null     \t\t\t\t            AS FLD15\n" +
            "        ,null     \t                        AS FLD16\n" +
            "        ,null              \t\t            AS FLD17\n" +
            "\t\t,null              \t                AS FLD18\n" +
            "\t\t,null               \t            AS FLD19\n" +
            "        ,null         \t\t                AS FLD20\n" +
            "        ,null                           \tas FLD21                     \n" +
            "        ,null                    \t        as FLD22                     \n" +
            "        ,null                    \t        as FLD23\t\t\t\t\t    \n" +
            "        ,null                               as FLD24\t\t\t\t\t  \n" +
            "        ,null                        \t    as FLD25\t\t\t\t\t  \n" +
            "        ,null                        \t    as FLD26\t\t\t\t\t  \n" +
            "        ,null                               as FLD27\n" +
            "        ,null                               as FLD28\n" +
            "        ,null                               as FLD29\n" +
            "        ,null                               as FLD30\n" +
            "        ,null                               as FLD31\n" +
            "        ,null                               as FLD32\n" +
            "        ,null                               as FLD33\n" +
            "        ,null                               as FLD34\n" +
            "        ,null                               as FLD35\n" +
            "        ,null                               as FLD36\n" +
            "        ,null                               as FLD37\n" +
            "        ,null                               as FLD38\n" +
            "        ,null                               as FLD39\n" +
            "        ,null                               as FLD40\n" +
            "        ,null                               as FLD41\n" +
            "        ,null                               as FLD42\n" +
            "        ,null                               as FLD43\n" +
            "        ,null                               as FLD44\n" +
            "        ,null                               as FLD45\n" +
            "        ,null                               as FLD46\n" +
            "        ,null                               as FLD47\n" +
            "        ,null                               as FLD48\n" +
            "        ,null                               as FLD49\n" +
            "        ,null                               as FLD50\n" +
            "\t\t,NULL                               AS FLD51\n" +
            "        ,NULL                               AS FLD52\n" +
            "        ,NULL                               AS FLD53\n" +
            "        ,NULL                               AS FLD54\n" +
            "        ,NULL                               AS FLD55\n" +
            "        ,NULL                               AS FLD56\n" +
            "        ,NULL                               AS FLD57\n" +
            "        ,NULL                               AS FLD58\n" +
            "        ,NULL                               AS FLD59\n" +
            "\t\t,NULL                               AS FLD60\n" +
            "        ,GETDATE() AS TIME_POKE\n" +
            "        ,'${bizdate1}'\n" +
            "        ,'JCZX_000098'\n" +
            "FROM    (\n" +
            "select \n" +
            "provincial_id\n" +
            "\t,mgt_org_code\n" +
            "\t,cust_no\n" +
            "\t,cust_name\n" +
            "\t,ind_cls   --行业分类\n" +
            "\t,ec_categ  --用电类别\n" +
            "\t,VOLTAGE   --电压等级\n" +
            "\t,CTRT_CAP  --合同容量\n" +
            "\t,RUN_CAP   --运行容量\n" +
            "\t,cust_id\n" +
            "\t,concat(substr(pers_info,1,6),'********',substr(pers_info,-4)) as pers_info --证件号码\n" +
            "\t,error_type\n" +
            "from (\n" +
            "select \n" +
            "     provincial_id\n" +
            "\t,mgt_org_code\n" +
            "\t,cust_no\n" +
            "\t,cust_name\n" +
            "\t,ind_cls   --行业分类\n" +
            "\t,ec_categ  --用电类别\n" +
            "\t,VOLTAGE   --电压等级\n" +
            "\t,CTRT_CAP  --合同容量\n" +
            "\t,RUN_CAP   --运行容量\n" +
            "\t,cust_id\n" +
            "\t,pers_info --联系电话\n" +
            "\t,error_type\n" +
            "\t,rank() over(partition by provincial_id order by pers_info desc) as rank1\n" +
            "from error_01\n" +
            ")\n" +
            "where rank1 = 1\n" +
            "union \n" +
            "select \n" +
            "provincial_id\n" +
            "\t,mgt_org_code\n" +
            "\t,cust_no\n" +
            "\t,cust_name\n" +
            "\t,ind_cls   --行业分类\n" +
            "\t,ec_categ  --用电类别\n" +
            "\t,VOLTAGE   --电压等级\n" +
            "\t,CTRT_CAP  --合同容量\n" +
            "\t,RUN_CAP   --运行容量\n" +
            "\t,cust_id\n" +
            "\t,concat(substr(pers_info,1,3),'****',substr(pers_info,-4)) as pers_info --联系电话\n" +
            "\t,error_type\n" +
            "from (\n" +
            "select \n" +
            "     provincial_id\n" +
            "\t,mgt_org_code\n" +
            "\t,cust_no\n" +
            "\t,cust_name\n" +
            "\t,ind_cls   --行业分类\n" +
            "\t,ec_categ  --用电类别\n" +
            "\t,VOLTAGE   --电压等级\n" +
            "\t,CTRT_CAP  --合同容量\n" +
            "\t,RUN_CAP   --运行容量\n" +
            "\t,cust_id\n" +
            "\t,pers_info --联系电话\n" +
            "\t,error_type\n" +
            "\t,rank() over(partition by provincial_id order by pers_info desc) as rank1\n" +
            "from error_02\n" +
            ")\n" +
            "where rank1 = 1\n" +
            ")t8\n" +
            "left join \n" +
            "(\n" +
            "    SELECT\n" +
            "        mgt_org_code,\n" +
            "        country_org_no,\n" +
            "        city_org_no,\n" +
            "        prov_org_no,\n" +
            "        ds\n" +
            "    FROM ads_cst_yx2_yxjc_mgt_org_2_df\n" +
            "    WHERE ds = '${bizdate_ymd1}'\n" +
            ") tb3\n" +
            "on t8.MGT_ORG_CODE = tb3.MGT_ORG_CODE\n" +
            "left join \n" +
            "        (\n" +
            "            SELECT \n" +
            "                code_Cls_Type\n" +
            "                ,code_Cls_Val  --码值\n" +
            "                ,code_Cls_Val_Name  \n" +
            "                ,provincial_id\n" +
            "                ,ds\n" +
            "            FROM pro_dwd_cms2_prod.UN94_02_CMS20_Code_Cls_Val\n" +
            "            where ds = '${bizdate_ymd}'\n" +
            "            and valid_flag = '02'  --生效\n" +
            "            and code_Cls_Type = 'indElecCls'  --行业分类\n" +
            "        ) ccv1\n" +
            "on t8.provincial_id = ccv1.provincial_id\n" +
            "and t8.ind_cls = ccv1.code_Cls_Val\n" +
            "left join \n" +
            "        (\n" +
            "            SELECT \n" +
            "                code_Cls_Type\n" +
            "                ,code_Cls_Val  --码值\n" +
            "                ,code_Cls_Val_Name\n" +
            "                ,provincial_id\n" +
            "                ,ds\n" +
            "            FROM pro_dwd_cms2_prod.UN94_02_CMS20_Code_Cls_Val\n" +
            "            where ds = '${bizdate_ymd}'\n" +
            "            and valid_flag = '02'  --生效\n" +
            "            and code_Cls_Type = 'ecCateg'  --用电类别\n" +
            "        ) ccv2\n" +
            "on t8.provincial_id = ccv2.provincial_id\n" +
            "and t8.ec_Categ = ccv2.code_Cls_Val\n" +
            "left join \n" +
            "        (\n" +
            "            SELECT \n" +
            "                code_Cls_Type\n" +
            "                ,code_Cls_Val  --码值\n" +
            "                ,code_Cls_Val_Name\n" +
            "                ,provincial_id\n" +
            "                ,ds\n" +
            "            FROM pro_dwd_cms2_prod.UN94_02_CMS20_Code_Cls_Val\n" +
            "            where ds = '${bizdate_ymd}'\n" +
            "            and valid_flag = '02'  --生效\n" +
            "            and code_Cls_Type = 'baseVolt'  --电压等级\n" +
            "        ) ccv3\n" +
            "on t8.provincial_id = ccv3.provincial_id\n" +
            "and t8.voltage = ccv3.code_Cls_Val;\n";


    public static final String ZT_1 ="with work_order_data as\n" +
            " (select WK_ORDER_HIS.provincial_id, --省单位编号\n" +
            "         WK_ORDER_HIS.mgt_Org_Code, --供电单位编号\n" +
            "         WK_ORDER_HIS.wk_order_no, -- 业扩工单编号\n" +
            "         WK_ORDER_HIS.wk_Order_Stat,\n" +
            "         WK_ORDER_HIS.creat_Time,\n" +
            "         WK_ORDER_HIS.acmp_Time,\n" +
            "         WK_ORDER_HIS.wk_Order_ID\n" +
            "    from pro_dwd_cms2_prod.UN94_02_CMS20_WK_ORDER_HIS WK_ORDER_HIS\n" +
            "   where WK_ORDER_HIS.ds  = \"${bizdate_ymd}\"\n" +
            "     and WK_ORDER_HIS.wk_Order_Stat = \"05\"\n" +
            "     and WK_ORDER_HIS.acmp_Time >= DATEADD(TO_DATE(CONCAT(\"${bizdate1}\", \"26\"), \"yyyymmdd\"), -1, \"MM\")\n" +
            "     and WK_ORDER_HIS.acmp_Time < TO_DATE(CONCAT(\"${bizdate1}\", \"26\"), \"yyyymmdd\")\n" +
            "  ),\n" +
            "cons_data as\n" +
            " (select distinct Bus_App_Form.provincial_id,--省单位编号\n" +
            "                  Bus_App_Form.mgt_Org_Code,--供电单位编号\n" +
            "                  Bus_App_Form.bus_type,--业扩申请类型\n" +
            "                  a.wk_order_no, -- 业扩工单编号\n" +
            "                  CUST_ELEC_APP_REC.cust_No,\n" +
            "                  CUST_ELEC_APP_REC.cust_Name,\n" +
            "                  CUST_ELEC_APP_REC.ec_Addr,\n" +
            "                  CUST_ELEC_APP_REC.ind_Cls,\n" +
            "                  Bus_App_Form.app_Mode,-- 受理渠道\n" +
            "                  CUST_ELEC_APP_REC.voltage, --电压等级\n" +
            "                  CUST_ELEC_APP_REC.ec_Categ, --用电类别\n" +
            "                  (select count(distinct conn_No) \n" +
            "                     from pro_dwd_cms2_prod.un94_02_cms20_CONNECTION_APP_REC CONNECTION_APP_REC\n" +
            "                    where Bus_App_Form.provincial_id = CONNECTION_APP_REC.provincial_id\n" +
            "                      and Bus_App_Form.app_no = CONNECTION_APP_REC.app_No\n" +
            "                      and CONNECTION_APP_REC.ARCH_STATUS <> \"04\"\n" +
            "                      and CONNECTION_APP_REC.ds  = \"${bizdate_ymd}\"\n" +
            "                  ) PS_NUM,-- 电源数量\n" +
            "                  CUST_ELEC_APP_REC.impt_Lv,-- 重要性等级\n" +
            "                  a.acmp_Time,-- 归档时间\n" +
            "                  CUST_ELEC_APP_REC.CUST_ID,\n" +
            "                  A.wk_Order_ID,\n" +
            "                  Bus_App_Form.bus_App_Form_ID,\n" +
            "                  a.wk_Order_Stat,\n" +
            "                  a.creat_Time,\n" +
            "\t\t\t\t  CUST_ELEC_APP_REC.cust_App_Rec_ID\n" +
            "    from pro_dwd_cms2_prod.UN94_02_CMS20_BUS_APP_FORM      Bus_App_Form,\n" +
            "         pro_dwd_cms2_prod.UN94_02_CMS20_CUST_ELEC_APP_REC CUST_ELEC_APP_REC,\n" +
            "         work_order_data                                   a\n" +
            "   where Bus_App_Form.provincial_id = CUST_ELEC_APP_REC.provincial_id\n" +
            "     and Bus_App_Form.bus_App_Form_ID = CUST_ELEC_APP_REC.bus_App_Form_ID\n" +
            "     and Bus_App_Form.provincial_id = a.provincial_id\n" +
            "     AND Bus_App_Form.app_no = a.wk_order_no\n" +
            "     and Bus_App_Form.bus_type in (\"010101\", \"010102\") -- 010101\t高压新装\n" +
            "     and Bus_App_Form.ds  = \"${bizdate_ymd}\"\n" +
            "     and CUST_ELEC_APP_REC.ds  = \"${bizdate_ymd}\"\n" +
            "\t and Bus_App_Form.app_stat  =\"05\"\n" +
            " ),  \n" +
            " \n" +
            "cons_data1 as\n" +
            " (select distinct a.provincial_id,--省单位编号\n" +
            "                  a.mgt_Org_Code,--供电单位编号\n" +
            "                  a.bus_type,--业扩申请类型\n" +
            "                  a.wk_order_no, -- 业扩工单编号\n" +
            "                  a.cust_No,\n" +
            "                  a.cust_Name,\n" +
            "                  a.ec_Addr,\n" +
            "                  a.ind_Cls,\n" +
            "                  a.app_Mode,-- 受理渠道\n" +
            "                  a.voltage, --电压等级\n" +
            "                  a.ec_Categ, --用电类别\n" +
            "                  a.PS_NUM,-- 电源数量\n" +
            "                  a.impt_Lv,-- 重要性等级\n" +
            "                  a.acmp_Time,-- 归档时间\n" +
            "                  a.CUST_ID,\n" +
            "                  A.wk_Order_ID,\n" +
            "                  a.bus_App_Form_ID,\n" +
            "                  a.wk_Order_Stat,\n" +
            "                  a.creat_Time,\n" +
            "\t\t\t\t  t1.HIGH_STD_FARM_LAND_FLAG,   --高标准农田标志\n" +
            "\t\t\t\t  t1.DEV_FAC_TYPE,             --设备设施类型\n" +
            "\t\t\t\t  t2.ext_Attr_Code\n" +
            "            from cons_data a\n" +
            "            left join \n" +
            "\t\t\t(select distinct \n" +
            "\t\t        \t provincial_id\n" +
            "\t\t\t\t\t,app_no\n" +
            "\t\t\t\t\t,HIGH_STD_FARM_LAND_FLAG   --高标准农田标志\n" +
            "\t\t\t\t\t,DEV_FAC_TYPE             --设备设施类型\n" +
            "\t\t\t from pro_dwd_cms2_prod.dwd_cst_cust_irri_dra_fac_app\n" +
            "\t\t\t where ds = \"${bizdate_ymd}\"\n" +
            "\t\t\t) t1\n" +
            "            on a.provincial_id = t1.provincial_id\n" +
            "            and a.wk_order_no = t1.app_no\n" +
            "            left join \n" +
            "\t\t\t(select distinct \n" +
            "\t\t\t        provincial_id\n" +
            "\t\t\t\t\t,cust_App_Rec_ID\n" +
            "\t\t\t\t\t,case when ext_Attr_Code = \"01\" then \"02\"\n" +
            "                       else \"01\" end as ext_Attr_Code                  --充电桩标志\n" +
            "\t\t\t from pro_dwd_cms2_prod.un94_02_cms20_ext_attr\n" +
            "\t\t\t where ds = \"${bizdate_ymd}\"\n" +
            "\t\t\t) t2\n" +
            "            on a.provincial_id = t2.provincial_id\n" +
            "\t\t\tand a.cust_App_Rec_ID = t2.cust_App_Rec_ID\n" +
            "),                                                        \n" +
            "proc_instance as\n" +
            " (select distinct \"1\" onece,\n" +
            "                  a.provincial_id,--省单位编号\n" +
            "                  a.mgt_Org_Code,--供电单位编号\n" +
            "                  a.bus_type,--业扩申请类型\n" +
            "                  a.wk_order_no, -- 业扩工单编号\n" +
            "                  a.cust_No,\n" +
            "                  a.cust_Name,\n" +
            "                  a.ec_Addr,\n" +
            "                  a.app_Mode,-- 受理渠道\n" +
            "                  a.voltage, --电压等级\n" +
            "                  a.ec_Categ, --用电类别\n" +
            "                  a.PS_NUM,-- 电源数量\n" +
            "                  a.impt_Lv,-- 重要性等级\n" +
            "                  a.acmp_Time,-- 归档时间\n" +
            "                  a.CUST_ID,\n" +
            "                  A.wk_Order_ID,\n" +
            "                  a.bus_App_Form_ID,\n" +
            "                  a.wk_Order_Stat,\n" +
            "                  a.creat_Time,\n" +
            "                  PROC_ACCT_HIS.proc_Acct_ID,\n" +
            "\t\t\t\t  a.HIGH_STD_FARM_LAND_FLAG,   --高标准农田标志\n" +
            "                  a.DEV_FAC_TYPE,              --设备设施类型\n" +
            "                  a.ext_Attr_Code,             --充电桩标志\n" +
            "                  a.ind_cls                    --行业分类\n" +
            "    from cons_data1                               a,\n" +
            "         pro_dwd_cms2_prod.UN94_02_CMS20_PROC_ACCT_HIS PROC_ACCT_HIS\n" +
            "   where a.provincial_id = PROC_ACCT_HIS.provincial_id\n" +
            "     and a.wk_Order_ID = PROC_ACCT_HIS.wk_Order_ID\n" +
            "     and PROC_ACCT_HIS.ds  = \"${bizdate_ymd}\"\n" +
            "),\n" +
            "step_data as(\n" +
            "          select STEP_REC_his.provincial_id,\n" +
            "                 STEP_REC_his.proc_Acct_ID,\n" +
            "                 STEP_REC_his.step_Acct_ID,\n" +
            "                 STEP_REC_his.step_Stat,\n" +
            "                 STEP_REC_his.step_No,\n" +
            "                 STEP_REC_his.step_Name,\n" +
            "                 STEP_REC_his.rcv_Time,\n" +
            "                 STEP_REC_his.acmp_Time\n" +
            "            from pro_dwd_cms2_prod.UN94_02_CMS20_STEP_REC_his STEP_REC_his\n" +
            "           where EXISTS (select 1 from proc_instance a\n" +
            "                                 where a.provincial_id = STEP_REC_his.provincial_id\n" +
            "                                   and a.proc_Acct_ID = STEP_REC_his.proc_Acct_ID)\n" +
            "             and STEP_REC_his.ds  = \"${bizdate_ymd}\"\n" +
            "),\n" +
            "acpt_cur as\n" +
            " (select provincial_id,\n" +
            "         proc_Acct_ID,\n" +
            "         step_Stat,\n" +
            "         step_No,\n" +
            "         step_Name,\n" +
            "         rcv_Time,\n" +
            "         acmp_Time,\n" +
            "         ROW_NUMBER() OVER(PARTITION BY provincial_id,proc_Acct_ID ORDER BY cur_time desc) AS cnt \n" +
            "    from (select provincial_id,\n" +
            "                 proc_Acct_ID,\n" +
            "                 step_Stat,\n" +
            "                 step_No,\n" +
            "                 step_Name,\n" +
            "                 rcv_Time,\n" +
            "                 acmp_Time,\n" +
            "                 DATEDIFF(acmp_Time, rcv_Time, \"ss\") cur_time\n" +
            "            from step_data\n" +
            "           where step_Stat = \"03\"\n" +
            "             and step_No in (\"BAC_0100103\",\"BAC_0100104\",\"BAC_0100131\") -- 业务受理 线上业务受理 营业厅受理\n" +
            "             )            \n" +
            ")\n" +
            "select \n" +
            "        concat(\"11101\",substr(\"${bizdate1}\",3,4),\"000002\",100000000+ROW_NUMBER() OVER()) as data_id  --数据标识\n" +
            "        ,concat(\"11101\",\"000002\",substr(\"${bizdate1}\",3,4),100000000+ROW_NUMBER() OVER()) as ABNOR_ID      --内控明细标识\n" +
            "        ,case when provincial_id = \"10500001\" then \"11102\"      --北京\n" +
            "              when provincial_id = \"10000028\" then \"12101\"      --天津\n" +
            "              when provincial_id = \"10372269\" then \"13102\"      --河北\n" +
            "              when provincial_id = \"10687637\" then \"16101\"      --冀北\n" +
            "              when provincial_id = \"10583026\" then \"14101\"      --山西\n" +
            "              when provincial_id = \"10554019\" then \"37101\"      --山东 \n" +
            "              when provincial_id = \"10010340\" then \"31102\"      --上海\n" +
            "              when provincial_id = \"10430001\" then \"32101\"      --江苏\n" +
            "              when provincial_id = \"10262690\" then \"33101\"      --浙江\n" +
            "              when provincial_id = \"10330027\" then \"34101\"      --安徽\n" +
            "              when provincial_id = \"10220116\" then \"35101\"      --福建\n" +
            "              when provincial_id = \"10467828\" then \"42102\"      --湖北\n" +
            "              when provincial_id = \"10175327\" then \"43101\"      --湖南\n" +
            "              when provincial_id = \"10522001\" then \"41101\"      --河南\n" +
            "              when provincial_id = \"10090121\" then \"36101\"      --江西 \n" +
            "              when provincial_id = \"10300001\" then \"51101\"      --四川\n" +
            "              when provincial_id = \"10060001\" then \"50101\"      --重庆\n" +
            "              when provincial_id = \"10130001\" then \"21102\"      --辽宁 \n" +
            "              when provincial_id = \"10200000\" then \"22101\"      --吉林\n" +
            "              when provincial_id = \"10400000\" then \"23101\"      --黑龙\n" +
            "              when provincial_id = \"10045073\" then \"15101\"      --蒙东\n" +
            "              when provincial_id = \"10750029\" then \"61102\"      --陕西\n" +
            "              when provincial_id = \"10778300\" then \"62101\"      --甘肃\n" +
            "              when provincial_id = \"10798565\" then \"63101\"      --青海 \n" +
            "              when provincial_id = \"10606629\" then \"64101\"      --宁夏\n" +
            "              when provincial_id = \"10621001\" then \"65101\"      --新疆\n" +
            "              when provincial_id = \"10520001\" then \"54101\"      --西藏  \n" +
            "         else provincial_id\n" +
            "         end                      as PROV_ORG_NO                    --省公司单位编码\n" +
            "        ,null                  \t\n" +
            "        ,null                  \t\n" +
            "        ,mgt_Org_Code             as PS_ORG_NO              --供电单位\n" +
            "        ,\"${bizdate1}\"            as STATS_CYC              --异常年月\n" +
            "        ,wk_order_no              as SUBJ_KEY_FIELD         --关键信息值（工单编号）\t\t\n" +
            "\t\t,cust_no                  AS CUST_NO            --用户编号\n" +
            "        ,\"03\"                     AS RULE_TYPE          --规则类型\n" +
            "        ,\"CHK_ZB_01000002\"        AS RULE_NO            --规则编号\n" +
            "        ,\"VER_20240101_0001\"      AS RECORD_VER_NO      --备案版本号\n" +
            "        ,\"11101\"                  AS PROORG_NO_DEPLOY   --部署省单位\n" +
            "        ,\"11101\"                  AS ORG_NO_DEPLOY      --部署单位编号\n" +
            "        ,\"01\"                     AS ORG_NO_DEPLOY_LV   --部署单位区域层级\n" +
            "        ,\"02\"                     AS STATS_CYC_TYPE     --规则执行频度(原统计周期类型)\n" +
            "        ,\"02\"                     AS CHK_OBJECT         --规则核查对象(原关键信息类别)\t\t\n" +
            "        ,\"01\"                     AS DATA_SRC           --数据来源\t\t\n" +
            "        ,getdate()                as TIME_POKE              --时间戳\n" +
            "        ,bus_Type                 as FLD_1                  --业务类型\n" +
            "        ,ps_num_code              as FLD_2                  --电源类型\n" +
            "        ,cust_no              as FLD_3                  --客户编号\n" +
            "        ,cust_name            as FLD_4                  --客户名称\n" +
            "        ,ec_Addr              as FLD_5                  --用电地址\n" +
            "        ,ec_categ             as FLD_6                  --用电类别  \n" +
            "        ,SUBSTR(ec_categ,1,2) as FLD_7                  --用电类别(大类)\n" +
            "        ,voltage              as FLD_8                  --电压等级\n" +
            "        ,case when trim(voltage) in (\"0115\",\"0116\",\"0117\",\"0118\",\"0119\") then \"01\"\t\t\t        --业扩报装电压等级大类\n" +
            "\t          when trim(voltage) in (\"0120\",\"0121\",\"0142\") then \"02\"\n" +
            "\t          when trim(voltage) in (\"0122\",\"0123\",\"0124\",\"0125\",\"0126\",\"0127\",\"0128\") then \"03\"\n" +
            "\t    else voltage\n" +
            "\t    end                         as FLD_9                   \n" +
            "\t\t,HIGH_STD_FARM_LAND_FLAG    as FLD_10   --高标准农田标志              \t\n" +
            "        ,DEV_FAC_TYPE               as FLD_11   --设备设施类型                 \t\n" +
            "        ,ext_Attr_Code              as FLD_12   --充电桩标志                 \t\n" +
            "        ,ind_cls                    as FLD_13   --行业分类                \t\n" +
            "        ,null                       as FLD_14                 \t\n" +
            "        ,null                       as FLD_15                 \t\n" +
            "        ,null                       as FLD_16                 \t\n" +
            "        ,null                       as FLD_17                 \t\n" +
            "        ,null                       as FLD_18                 \t\n" +
            "        ,null                       as FLD_19                 \t\n" +
            "        ,null                       as FLD_20                 \t\n" +
            "        ,acpt_worktime              as FLD_21                 --受理环节时长（工作日）\n" +
            "        ,1                          as FLD_22                 --环节时限要求\t\n" +
            "        ,acpt_worktime - 1          as FLD_23                 --环节超期天数（工作日）\t\t\n" +
            "        ,null                       as FLD_24                 \t\n" +
            "        ,null                       as FLD_25                 \t\n" +
            "        ,null                       as FLD_26                 \t\n" +
            "        ,null                       as FLD_27                 \t\n" +
            "        ,null                       as FLD_28                 \t\n" +
            "        ,null                       as FLD_29                 \t\n" +
            "        ,null                       as FLD_30                 \t\n" +
            "        ,null                       as FLD_31                 \t\n" +
            "        ,null                       as FLD_32                 \t\n" +
            "        ,null                       as FLD_33                 \t\n" +
            "        ,null                       as FLD_34                 \t\n" +
            "        ,null                       as FLD_35                 \t\n" +
            "        ,null                       as FLD_36                 \t\n" +
            "        ,null                       as FLD_37                 \t\n" +
            "        ,null                       as FLD_38                 \t\n" +
            "        ,null                       as FLD_39                 \t\n" +
            "        ,null                       as FLD_40                 \t\n" +
            "        ,acpt_Beg_Time              as FLD_41                --首次受理开始时间\n" +
            "        ,acpt_Acmp_Time             as FLD_42                --最后一次受理结束时间\n" +
            "        ,acmp_Time                  as FLD_43                --流程归档时间\n" +
            "        ,null                       as FLD_44                 \t\n" +
            "        ,null                       as FLD_45                 \t\n" +
            "        ,null                       as FLD_46                 \t\n" +
            "        ,null                       as FLD_47                 \t\n" +
            "        ,null                       as FLD_48                 \t\n" +
            "        ,null                       as FLD_49                 \t\n" +
            "        ,null                       as FLD_50                 \t\n" +
            "        ,\"${bizdate1}\"              as ds \n" +
            "        ,\"CHK_ZB_01000002\"          as SUBJ_NO\n" +
            "from \n" +
            "     (select distinct  provincial_id,--省单位编号\n" +
            "                  mgt_Org_Code,--供电单位编号\n" +
            "                  bus_type,--业扩申请类型\n" +
            "                  wk_order_no, -- 业扩工单编号\n" +
            "                  wk_Order_Stat,\n" +
            "                  cust_No,\n" +
            "                  cust_Name,\n" +
            "                  ec_Addr,\n" +
            "                  app_Mode,-- 受理渠道\n" +
            "                  voltage, --电压等级\n" +
            "                  ec_Categ, --用电类别\n" +
            "                  ps_Num,-- 电源数量\n" +
            "                  ps_num_code,\n" +
            "                  impt_Lv,-- 重要性等级\n" +
            "\n" +
            "                  acpt_Beg_Time,-- 业务受理环节开始时间\n" +
            "\t\t\t\t  acpt_Acmp_Time,-- 业务受理环节完成时间\n" +
            "                  acpt_worktime,\t-- 业务受理时长\n" +
            "                  CUST_ID,\n" +
            "                  wk_Order_ID,\n" +
            "                  bus_App_Form_ID,\n" +
            "                  creat_Time,\n" +
            "                  acmp_Time,-- 归档时间\n" +
            "\t\t\t\t  HIGH_STD_FARM_LAND_FLAG,   --高标准农田标志\n" +
            "                  DEV_FAC_TYPE,              --设备设施类型\n" +
            "                  ext_Attr_Code,             --充电桩标志\n" +
            "                  ind_cls                    --行业分类\n" +
            "from\n" +
            " ( \n" +
            "select distinct  a.provincial_id,--省单位编号\n" +
            "                  a.mgt_Org_Code,--供电单位编号\n" +
            "                  a.bus_type,--业扩申请类型\n" +
            "                  a.wk_order_no, -- 业扩工单编号\n" +
            "                  a.wk_Order_Stat,\n" +
            "                  a.cust_No,\n" +
            "                  a.cust_Name,\n" +
            "                  a.ec_Addr,\n" +
            "                  a.app_Mode,-- 受理渠道\n" +
            "                  a.voltage, --电压等级\n" +
            "                  a.ec_Categ, --用电类别\n" +
            "                  case when a.ps_Num = 1 then \"02\" \n" +
            "\t\t\t           when a.ps_Num = 2 then \"01\" \n" +
            "\t\t\t           when a.ps_Num > 2 then \"03\" else null\t\n" +
            "                  end  as ps_num_code,-- 电源数量\n" +
            "                  a.ps_Num,\n" +
            "                  a.impt_Lv,-- 重要性等级\n" +
            "\n" +
            "                  acpt_cur.rcv_Time acpt_Beg_Time,-- 业务受理环节开始时间\n" +
            "\t\t\t\t  acpt_cur.acmp_Time acpt_Acmp_Time,-- 业务受理环节完成时间\n" +
            "                  DATEDIFF(to_date(TO_CHAR(acpt_cur.acmp_Time,\"YYYYMMDD\"),\"YYYYMMDD\"), to_date(TO_CHAR(acpt_cur.rcv_Time,\"YYYYMMDD\"),\"YYYYMMDD\"), \"dd\")  \n" +
            "                  - (select count(distinct holiday_date)\n" +
            "                      from (select \"1\" AS onece, holiday_date\n" +
            "                              from ads_cst_yx2_holiday_date_yf) hld\n" +
            "                     where hld.holiday_date >=\n" +
            "                           to_char(acpt_cur.rcv_Time, \"yyyymmdd\")\n" +
            "                       and hld.holiday_date <=\n" +
            "                           to_char(acpt_cur.acmp_Time, \"yyyymmdd\")\n" +
            "                       and hld.onece = a.onece) as acpt_worktime,\t-- 业务受理时长\n" +
            "                  a.CUST_ID,\n" +
            "                  a.wk_Order_ID,\n" +
            "                  a.bus_App_Form_ID,\n" +
            "                  a.creat_Time,\n" +
            "                  a.acmp_Time,-- 归档时间\n" +
            "\t\t\t\t  a.HIGH_STD_FARM_LAND_FLAG,   --高标准农田标志\n" +
            "                  a.DEV_FAC_TYPE,              --设备设施类型\n" +
            "                  a.ext_Attr_Code,             --充电桩标志\n" +
            "                  a.ind_cls                    --行业分类\n" +
            "             from proc_instance a \n" +
            "        LEFT join acpt_cur\n" +
            "               on a.provincial_id = acpt_cur.provincial_id\n" +
            "              AND a.proc_Acct_ID = acpt_cur.proc_Acct_ID\n" +
            "              AND acpt_cur.cnt = 1\n" +
            "    )\n" +
            "where acpt_worktime > 1\n" +
            "     )\n" +
            ";\n";
}


