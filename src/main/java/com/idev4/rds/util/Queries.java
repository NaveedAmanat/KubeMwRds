package com.idev4.rds.util;

public class Queries {

    public static final String FUND_STATMENT_HEADER = "  SELECT p.PRD_CMNT,  nvl(SUM (dsb.LOANS),0) dsb_loans,  \r\n"
            + "nvl(SUM (dsb.DSB_AMT),0) dsb_amt,  nvl(SUM (rec.rec_amt),0) rec,  nvl(SUM (dip.AMT),0) dip,  nvl(SUM (ip.AMT),0) IP,  \r\n"
            + "nvl(SUM (ddoc.AMT),0) ddoc,  nvl(SUM (doc.AMT),0) doc,  nvl(SUM (dtf.AMT),0) dtf,  nvl(SUM (tf.AMT),0) tf,  \r\n"
            + "nvl(SUM (dkszb.AMT),0) dkszb,  nvl(SUM (kszb.AMT),0) kszb,  nvl(SUM (fc.AMT),0) fc,  nvl(SUM (la1.AMT),0) LA  \r\n"
            + "FROM MW_PRD_GRP PG,  MW_PRD P,    ( SELECT la.PRD_SEQ,  nvl(COUNT (DISTINCT la.LOAN_APP_SEQ),0) loans,  \r\n"
            + "nvl(SUM (dsbd.amt),0) dsb_amt  FROM MW_DSBMT_VCHR_HDR dsbh,  MW_DSBMT_VCHR_DTL dsbd,  mw_loan_app la,  \r\n"
            + "MW_PORT MP  WHERE dsbh.DSBMT_HDR_SEQ = dsbd.DSBMT_HDR_SEQ  AND dsbh.CRNT_REC_FLG = 1  \r\n"
            + "AND dsbd.CRNT_REC_FLG = 1  AND la.LOAN_APP_SEQ = dsbh.LOAN_APP_SEQ  AND LA.PORT_SEQ = MP.PORT_SEQ  \r\n"
            + "AND MP.CRNT_REC_FLG = 1  AND MP.BRNCH_SEQ = :branch  AND la.CRNT_REC_FLG = 1  \r\n"
            + "AND TO_DATE (dsbh.DSBMT_DT) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')  \r\n"
            + "GROUP BY la.PRD_SEQ) dsb,    (select prd_seq,         count(distinct LOAN_APP_SEQ) rcvrd_clnt,\r\n"
            + "        (sum(rec_pr) + sum(rec_sc)) rec_amt\r\n" + "        from\r\n" + "        (\r\n"
            + "        select ap.LOAN_APP_SEQ, ap.prd_seq, \r\n" + "            nvl(sum((\r\n"
            + "                select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_trx rht ,mw_rcvry_dtl rdtl\r\n"
            + "                left outer join mw_typs rtyp on rtyp.typ_seq=rdtl.chrg_typ_key and rtyp.crnt_rec_flg=1\r\n"
            + "                where rdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg=1\r\n"
            + "                and rht.RCVRY_TRX_SEQ = rdtl.RCVRY_TRX_SEQ and rht.crnt_rec_flg=1\r\n"
            + "                and (rdtl.chrg_typ_key=-1)\r\n"
            + "                and rht.PYMT_DT BETWEEN to_date(:frmdt) and to_date(:todt)\r\n" + "            )),0) rec_pr,\r\n"
            + "            nvl(sum((\r\n" + "                select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_trx rht ,mw_rcvry_dtl rdtl\r\n"
            + "                left outer join mw_typs rtyp on rtyp.typ_seq=rdtl.chrg_typ_key and rtyp.crnt_rec_flg=1\r\n"
            + "                where rdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg=1\r\n"
            + "                and rht.RCVRY_TRX_SEQ = rdtl.RCVRY_TRX_SEQ and rht.crnt_rec_flg=1\r\n"
            + "                and (rdtl.chrg_typ_key in (select mt.typ_seq from mw_typs mt where mt.typ_id = '0017' and mt.crnt_rec_flg = 1))\r\n"
            + "                and rht.PYMT_DT BETWEEN to_date(:frmdt) and to_date(:todt)\r\n" + "            )),0) rec_sc\r\n"
            + "            from mw_loan_app ap\r\n"
            + "            join mw_pymt_sched_hdr psh on psh.loan_app_seq=ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
            + "            join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1,\r\n"
            + "            mw_port mp\r\n" + "            where ap.PORT_SEQ = mp.PORT_SEQ\r\n" + "            and ap.crnt_rec_flg=1\r\n"
            + "            and mp.BRNCH_SEQ = :branch\r\n"
            + "            and not exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4\r\n"
            + "            and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt))               \r\n"
            + "            group by ap.prd_seq, ap.loan_app_seq\r\n" + "    )\r\n" + "    where (rec_pr > 0 or rec_sc > 0)\r\n"
            + "    group by prd_seq) rec,   \r\n" + "(  SELECT  get_enty_prd(ENTY_SEQ, ENTY_TYP, MB.BRNCH_SEQ) PRD_SEQ,  \r\n"
            + "nvl(count(get_clnt_seq (ENTY_SEQ, ENTY_TYP, MB.BRNCH_SEQ)),0) loans,  \r\n"
            + "nvl(sum(NVL ((CASE WHEN MJD.CRDT_DBT_FLG = 1 THEN MJD.AMT END), 0)),0) amt  \r\n"
            + "FROM MW_JV_HDR MJH, MW_JV_DTL MJD, MW_BRNCH MB  WHERE MJH.JV_HDR_SEQ = MJD.JV_HDR_SEQ  \r\n"
            + "AND MB.CRNT_REC_FLG = 1  AND MJD.AMT > 0  AND MJH.BRNCH_SEQ = :branch  \r\n"
            + "AND TO_DATE (MJH.JV_DT) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')  \r\n"
            + "AND MJD.GL_ACCT_NUM = '000.000.404714.00000'  group by get_enty_prd(ENTY_SEQ, ENTY_TYP, MB.BRNCH_SEQ)  \r\n"
            + "having get_enty_prd(ENTY_SEQ, ENTY_TYP, MB.BRNCH_SEQ) > 0    ) dip,    (   SELECT  \r\n"
            + "get_enty_prd(ENTY_SEQ, ENTY_TYP, MB.BRNCH_SEQ) PRD_SEQ,  \r\n"
            + "nvl(count(get_clnt_seq (ENTY_SEQ, ENTY_TYP, MB.BRNCH_SEQ)),0) loans,  \r\n"
            + "nvl(sum(NVL ((CASE WHEN MJD.CRDT_DBT_FLG = 0 THEN MJD.AMT END), 0)),0) amt  \r\n"
            + "FROM MW_JV_HDR MJH, MW_JV_DTL MJD, MW_BRNCH MB  WHERE MJH.JV_HDR_SEQ = MJD.JV_HDR_SEQ  \r\n"
            + "AND MB.CRNT_REC_FLG = 1  AND MJD.AMT > 0  AND MJH.BRNCH_SEQ = :branch  \r\n"
            + "AND TO_DATE (MJH.JV_DT) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')  \r\n"
            + "AND MJD.GL_ACCT_NUM = '000.000.404714.00000'  group by get_enty_prd(ENTY_SEQ, ENTY_TYP, MB.BRNCH_SEQ)  \r\n"
            + "having get_enty_prd(ENTY_SEQ, ENTY_TYP, MB.BRNCH_SEQ) > 0    ) ip,    ( SELECT  \r\n"
            + "get_enty_prd(ENTY_SEQ, ENTY_TYP,  MJH.BRNCH_SEQ) PRD_SEQ,  \r\n"
            + "nvl(count(get_clnt_seq (ENTY_SEQ, ENTY_TYP,  MJH.BRNCH_SEQ)),0) loans,  \r\n"
            + "nvl(sum(NVL ((CASE WHEN MJD.CRDT_DBT_FLG = 1 THEN MJD.AMT END), 0)),0) amt   FROM MW_JV_HDR MJH, MW_JV_DTL MJD \r\n"
            + "WHERE MJH.JV_HDR_SEQ = MJD.JV_HDR_SEQ  AND MJD.AMT > 0   AND MJH.BRNCH_SEQ = :branch  \r\n"
            + "AND TO_DATE (MJH.JV_DT) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')  \r\n"
            + "AND MJD.GL_ACCT_NUM = '000.000.404713.00000'  group by get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ)  \r\n"
            + "having get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ) > 0   ) ddoc,   (   SELECT  \r\n"
            + "get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ) PRD_SEQ,  nvl(count(get_clnt_seq (ENTY_SEQ, ENTY_TYP, :branch)),0) loans,  \r\n"
            + "nvl(sum(NVL ((CASE WHEN MJD.CRDT_DBT_FLG = 0 THEN MJD.AMT END), 0)),0) amt  FROM MW_JV_HDR MJH, MW_JV_DTL MJD  \r\n"
            + "WHERE MJH.JV_HDR_SEQ = MJD.JV_HDR_SEQ  AND MJD.AMT > 0  AND MJH.BRNCH_SEQ =:branch  \r\n"
            + "AND TO_DATE (MJH.JV_DT) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')  \r\n"
            + "AND MJD.GL_ACCT_NUM = '000.000.404713.00000'  group by get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ)  \r\n"
            + "having get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ) > 0    ) doc,    (  SELECT  \r\n"
            + "get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ) PRD_SEQ,  \r\n"
            + "nvl(count(get_clnt_seq (ENTY_SEQ, ENTY_TYP, MJH.BRNCH_SEQ)),0) loans,  \r\n"
            + "nvl(sum(NVL ((CASE WHEN MJD.CRDT_DBT_FLG = 1 THEN MJD.AMT END), 0)),0) amt  FROM MW_JV_HDR MJH, MW_JV_DTL MJD  \r\n"
            + "WHERE MJH.JV_HDR_SEQ = MJD.JV_HDR_SEQ  AND MJD.AMT > 0  AND MJH.BRNCH_SEQ = :branch  \r\n"
            + "AND TO_DATE (MJH.JV_DT) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')  \r\n"
            + "AND MJD.GL_ACCT_NUM = '000.000.404715.00000'  group by get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ)  \r\n"
            + "having get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ) > 0    ) dtf,    (   SELECT  \r\n"
            + "get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ) PRD_SEQ,  nvl(count(get_clnt_seq (ENTY_SEQ, ENTY_TYP,:branch)),0) loans,  \r\n"
            + "nvl(sum(NVL ((CASE WHEN MJD.CRDT_DBT_FLG = 0 THEN MJD.AMT END), 0)),0) amt  FROM MW_JV_HDR MJH, MW_JV_DTL MJD  \r\n"
            + "WHERE MJH.JV_HDR_SEQ = MJD.JV_HDR_SEQ  AND MJD.AMT > 0  AND MJH.BRNCH_SEQ = :branch  \r\n"
            + "AND TO_DATE (MJH.JV_DT) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')  \r\n"
            + "AND MJD.GL_ACCT_NUM = '000.000.404715.00000'  group by get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ)  \r\n"
            + "having get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ) > 0    ) tf,    (   SELECT  \r\n"
            + "get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ) PRD_SEQ,  nvl(count(get_clnt_seq (ENTY_SEQ, ENTY_TYP, :branch)),0) loans,  \r\n"
            + "nvl(sum(NVL ((CASE WHEN MJD.CRDT_DBT_FLG = 1 THEN MJD.AMT END), 0)),0) amt  FROM MW_JV_HDR MJH, MW_JV_DTL MJD  \r\n"
            + "WHERE MJH.JV_HDR_SEQ = MJD.JV_HDR_SEQ  AND MJD.AMT > 0  AND MJH.BRNCH_SEQ = :branch  \r\n"
            + "AND TO_DATE (MJH.JV_DT) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')  \r\n"
            + "AND MJD.GL_ACCT_NUM = '000.000.404709.00000'  group by get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ)  \r\n"
            + "having get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ) > 0    ) dkszb,    (   SELECT  \r\n"
            + "get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ) PRD_SEQ,  nvl(count(get_clnt_seq (ENTY_SEQ, ENTY_TYP,:branch)),0) loans,  \r\n"
            + "nvl(sum(NVL ((CASE WHEN MJD.CRDT_DBT_FLG = 0 THEN MJD.AMT END), 0)),0) amt  FROM MW_JV_HDR MJH, MW_JV_DTL MJD  \r\n"
            + "WHERE MJH.JV_HDR_SEQ = MJD.JV_HDR_SEQ  AND MJD.AMT > 0  AND MJH.BRNCH_SEQ = :branch  \r\n"
            + "AND TO_DATE (MJH.JV_DT) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')  \r\n"
            + "AND MJD.GL_ACCT_NUM = '000.000.404709.00000'  group by get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ)  \r\n"
            + "having get_enty_prd(ENTY_SEQ, ENTY_TYP,MJH.BRNCH_SEQ) > 0    ) kszb,  \r\n"
            + "( SELECT PRD_SEQ, nvl(COUNT (loans),0) loans, nvl(SUM (5000),0) amt  FROM   (SELECT DISTINCT  \r\n"
            + "la.PRD_SEQ, me.EXP_REF loans, me.EXPNS_AMT amt  FROM mw_loan_app la, mw_exp me, mw_port mp  \r\n"
            + "WHERE la.CLNT_SEQ = me.EXP_REF  AND me.CRNT_REC_FLG = 1  AND LA.PORT_SEQ = MP.PORT_SEQ  \r\n"
            + "AND la.CRNT_REC_FLG = 1  AND MP.CRNT_REC_FLG = 1  AND MP.BRNCH_SEQ = :branch  \r\n"
            + "AND TO_DATE (me.CRTD_DT) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy') \r\n"
            + "and la.PRD_SEQ not in (2,3,5,13,14,29)  AND me.EXPNS_TYP_SEQ = 424)  GROUP BY PRD_SEQ  ) fc,   \r\n"
            + "(   select la.prd_seq, count(distinct la.loan_app_seq) loans,(nvl(sum(psd.PPAL_AMT_DUE),0) + nvl(sum(psd.TOT_CHRG_DUE),0)) amt \r\n"
            + "from mw_rcvry_trx trx  join mw_typs rcvry on rcvry.typ_seq = rcvry_typ_seq and rcvry.crnt_rec_flg = 1  \r\n"
            + "join mw_rcvry_dtl dtl on dtl.rcvry_trx_seq = trx.rcvry_trx_seq and dtl.crnt_rec_flg = 1  \r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_dtl_seq = dtl.pymt_sched_dtl_seq and psd.crnt_rec_flg = 1  \r\n"
            + "join mw_pymt_sched_hdr psh on psd.pymt_sched_hdr_seq = psh.pymt_sched_hdr_seq and psh.crnt_rec_flg = 1  \r\n"
            + "join mw_loan_app la on la.loan_app_seq = psh.loan_app_seq and la.crnt_rec_flg = 1  \r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq = la.loan_app_sts and val.crnt_rec_flg = 1 and val.del_flg = 0  \r\n"
            + "join mw_port prt on prt.port_seq = la.PORT_SEQ and prt.crnt_rec_flg = 1  \r\n"
            + "where trx.post_flg = 1 and trx.crnt_rec_flg = 1 and rcvry.typ_id ='0020'  \r\n"
            + "and to_date(trx.pymt_dt) between to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')  \r\n"
            + "and not exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4  \r\n"
            + "and ctl.loan_app_seq = la.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy')) \r\n"
            + "and dtl.chrg_typ_key=-1  group by la.prd_seq  ) la1  WHERE p.prd_seq = dsb.prd_seq(+)  \r\n"
            + "AND p.prd_seq = rec.prd_seq(+)  AND p.prd_seq = dip.prd_seq(+)  AND p.prd_seq = ip.prd_seq(+)  \r\n"
            + "AND p.prd_seq = ddoc.prd_seq(+)  AND p.prd_seq = doc.prd_seq(+)  AND p.prd_seq = dtf.prd_seq(+)  \r\n"
            + "AND p.prd_seq = tf.prd_seq(+)  AND p.prd_seq = dkszb.prd_seq(+)  AND p.prd_seq = kszb.prd_seq(+)  \r\n"
            + "AND p.prd_seq = fc.prd_seq(+)  AND p.prd_seq = la1.prd_seq(+)  AND p.PRD_GRP_SEQ = pg.PRD_GRP_SEQ  \r\n"
            + "and (dsb.LOANS > 0 or rec.rec_amt > 0 or dip.AMT > 0 or ip.AMT > 0 or ddoc.AMT > 0 or doc.AMT > 0 or  \r\n"
            + "dtf.AMT > 0 or tf.AMT > 0 or dkszb.AMT > 0 or kszb.AMT > 0 or fc.AMT > 0 or la1.AMT > 0)  AND pg.CRNT_REC_FLG = 1  \r\n"
            + "AND p.CRNT_REC_FLG = 1  GROUP BY p.PRD_CMNT, pg.prd_grp_seq  ORDER BY pg.prd_grp_seq";
    public static final String FUND_STATMENT_REPORT = "select grp, perd_key, frmDt frmDt, todt todt, typ_seq, typ_str,\r\n"
            + "bdgt_amt,\r\n" + "(case when CRDT_DBT_FLG = 1 and acct = '000.000.401101.00000' then amt else 0 end) cash_db_amt,\r\n"
            + "(case when CRDT_DBT_FLG = 0 and acct = '000.000.401101.00000' then amt else 0 end) cash_cr_amt,\r\n"
            + "(case when CRDT_DBT_FLG = 1 and acct is null then amt else 0 end) bnk_db_amt,\r\n"
            + "(case when CRDT_DBT_FLG = 0 and acct is null then amt else 0 end) bnk_cr_amt\r\n" + "from\r\n" + "(\r\n"
            + "select '1.Monthly Expenses' grp, mper.perd_key,  \r\n"
            + ":frmDt frmdt, :toDt todt, ptyp.typ_seq, ptyp.typ_str,mjd.CRDT_DBT_FLG,\r\n"
            + "max((select max(mjd1.GL_ACCT_NUM) from mw_jv_dtl mjd1 where mjd1.JV_HDR_SEQ = mjd.JV_HDR_SEQ and mjd1.GL_ACCT_NUM = '000.000.401101.00000')) acct,\r\n"
            + "nvl(max((select nvl(bdgt_amt,0) from mw_brnch_bdgt bdgt where bdgt.brnch_seq=mjh.brnch_seq and bdgt.bdgt_ctgry_key=ptyp.typ_seq and bdgt.bdgt_perd = perd_key and bdgt.del_flg=0)),0) bdgt_amt,  \r\n"
            + "sum(mjd.amt) amt \r\n" + "from mw_jv_hdr mjh, mw_jv_dtl mjd, mw_perd mper, mw_typs ptyp\r\n"
            + "where mjh.JV_HDR_SEQ = mjd.JV_HDR_SEQ\r\n" + "and mjh.BRNCH_SEQ = :brnch\r\n"
            + "and  ptyp.typ_seq not in (422) and to_date(mjh.jv_dt) between :frmDt and :toDt  \r\n"
            + "and mjd.GL_ACCT_NUM = ptyp.GL_ACCT_NUM\r\n"
            + "and ptyp.typ_id not in ('0005','0006','0007','0500','0299','0336','0338','0339','0343','0297',  \r\n"
            + "'0345','0346','0347','0351','0354','0356','0357','0358','0359',  \r\n"
            + "'0360','0361','0362','0363','0364','0365','0366','0342','0420',  \r\n"
            + "'0421','0423','0424','0450','0451','0452','0454','0455','0456','0008','0331') and ptyp.perd_flg=1 \r\n"
            + "and ptyp.typ_ctgry_key=2 \r\n" + "and mjh.BRNCH_SEQ = :brnch\r\n" + "and ptyp.CRNT_REC_FLG = 1\r\n"
            + "and mjh.ENTY_TYP = 'Expense'\r\n" + "and mper.perd_key=to_char(mjh.jv_dt,'yyyymm')\r\n"
            + "group by mper.perd_key,mper.perd_strt_dt,mper.perd_end_dt,ptyp.typ_seq,ptyp.typ_str,mjd.CRDT_DBT_FLG\r\n" + ")\r\n"
            + "union\r\n" + "select grp, qtr_key, frmDt , toDt, typ_seq, typ_str,\r\n" + "bdgt_amt,\r\n"
            + "(case when CRDT_DBT_FLG = 1 and acct = '000.000.401101.00000' then amt else 0 end) cash_db_amt,\r\n"
            + "(case when CRDT_DBT_FLG = 0 and acct = '000.000.401101.00000' then amt else 0 end) cash_cr_amt,\r\n"
            + "(case when CRDT_DBT_FLG = 1 and acct is null then amt else 0 end) bnk_db_amt,\r\n"
            + "(case when CRDT_DBT_FLG = 0 and acct is null then amt else 0 end) bnk_cr_amt\r\n" + "from\r\n" + "(\r\n"
            + "select '2.Quarterly Expenses' grp, mper.qtr_key,  \r\n"
            + "to_char(mper.qtr_strt_dt,'MON-yyyy') frmDt, to_char(mper.qtr_end_dt,'MON-yyyy') toDt,\r\n"
            + " ptyp.typ_seq, ptyp.typ_str,mjd.CRDT_DBT_FLG,\r\n"
            + "max((select max(mjd1.GL_ACCT_NUM) from mw_jv_dtl mjd1 where mjd1.JV_HDR_SEQ = mjd.JV_HDR_SEQ and mjd1.GL_ACCT_NUM = '000.000.401101.00000')) acct,\r\n"
            + "nvl(max((select max(bdgt_amt) from mw_brnch_bdgt bdgt where bdgt.brnch_seq=mjh.brnch_seq and bdgt.bdgt_ctgry_key=ptyp.typ_seq and bdgt.bdgt_perd = qtr_key and bdgt.del_flg=0)),0) bdgt_amt,  \r\n"
            + "sum(mjd.amt) amt \r\n" + "from mw_jv_hdr mjh, mw_jv_dtl mjd, mw_perd mper, mw_typs ptyp\r\n"
            + "where mjh.JV_HDR_SEQ = mjd.JV_HDR_SEQ\r\n" + "and mjh.BRNCH_SEQ = :brnch\r\n" + "and mjd.GL_ACCT_NUM = ptyp.GL_ACCT_NUM\r\n"
            + "and ptyp.typ_id not in ('0005','0006','0007','0500','0299','0336','0338','0339','0343','0297',  \r\n"
            + "'0345','0346','0347','0351','0354','0356','0357','0358','0359',  \r\n"
            + "'0360','0361','0362','0363','0364','0365','0366','0342','0420',  \r\n"
            + "'0421','0423','0424','0450','0451','0452','0454','0455','0456','0008','0331') and ptyp.perd_flg=2\r\n"
            + "and ptyp.typ_ctgry_key=2 \r\n" + "and mjh.BRNCH_SEQ = :brnch\r\n" + "and ptyp.CRNT_REC_FLG = 1\r\n"
            + "and mjh.ENTY_TYP = 'Expense'\r\n" + "and mper.perd_key=to_char(mjh.jv_dt,'yyyymm')\r\n"
            + "and mper.perd_key <= to_char(to_date(:toDt),'yyyymm') and mper.fin_yr = (select fin_yr from mw_perd where perd_key= to_char(to_date(:toDt),'yyyymm'))\r\n"
            + "group by mper.qtr_key,to_char(mper.qtr_strt_dt,'MON-yyyy'),to_char(mper.qtr_end_dt,'MON-yyyy'),ptyp.typ_seq,ptyp.typ_str,mjd.CRDT_DBT_FLG\r\n"
            + ")\r\n" + "union\r\n" + "select grp, BI_ANL_KEY, frmDt , toDt, typ_seq, typ_str,\r\n" + "bdgt_amt,\r\n"
            + "(case when CRDT_DBT_FLG = 1 and acct = '000.000.401101.00000' then amt else 0 end) cash_db_amt,\r\n"
            + "(case when CRDT_DBT_FLG = 0 and acct = '000.000.401101.00000' then amt else 0 end) cash_cr_amt,\r\n"
            + "(case when CRDT_DBT_FLG = 1 and acct is null then amt else 0 end) bnk_db_amt,\r\n"
            + "(case when CRDT_DBT_FLG = 0 and acct is null then amt else 0 end) bnk_cr_amt\r\n" + "from\r\n" + "(\r\n"
            + "select '3.Bi-Annual Expenses' grp, mper.BI_ANL_KEY,  \r\n"
            + "to_char(mper.BI_ANL_STRT_DT,'MON-yyyy') frmDt, to_char(mper.BI_ANL_end_DT,'MON-yyyy') toDt,\r\n"
            + " ptyp.typ_seq, ptyp.typ_str,mjd.CRDT_DBT_FLG,\r\n"
            + "max((select max(mjd1.GL_ACCT_NUM) from mw_jv_dtl mjd1 where mjd1.JV_HDR_SEQ = mjd.JV_HDR_SEQ and mjd1.GL_ACCT_NUM = '000.000.401101.00000')) acct,\r\n"
            + "nvl(max((select max(bdgt_amt) from mw_brnch_bdgt bdgt where bdgt.brnch_seq=mjh.brnch_seq and bdgt.bdgt_ctgry_key=ptyp.typ_seq and bdgt.bdgt_perd = BI_ANL_KEY and bdgt.del_flg=0)),0) bdgt_amt,  \r\n"
            + "sum(mjd.amt) amt \r\n" + "from mw_jv_hdr mjh, mw_jv_dtl mjd, mw_perd mper, mw_typs ptyp\r\n"
            + "where mjh.JV_HDR_SEQ = mjd.JV_HDR_SEQ\r\n" + "and mjh.BRNCH_SEQ = :brnch\r\n" + "and mjd.GL_ACCT_NUM = ptyp.GL_ACCT_NUM\r\n"
            + "and ptyp.typ_id not in ('0005','0006','0007','0500','0299','0336','0338','0339','0343','0297',  \r\n"
            + "'0345','0346','0347','0351','0354','0356','0357','0358','0359',  \r\n"
            + "'0360','0361','0362','0363','0364','0365','0366','0342','0420',  \r\n"
            + "'0421','0423','0424','0450','0451','0452','0454','0455','0456','0008','0331') and ptyp.perd_flg=3\r\n"
            + "and ptyp.typ_ctgry_key=2 \r\n" + "and mjh.BRNCH_SEQ = :brnch\r\n" + "and ptyp.CRNT_REC_FLG = 1\r\n"
            + "and mjh.ENTY_TYP = 'Expense'\r\n" + "and mper.perd_key=to_char(mjh.jv_dt,'yyyymm')\r\n"
            + "and mper.perd_key <= to_char(to_date(:toDt),'yyyymm') and mper.fin_yr = (select fin_yr from mw_perd where perd_key= to_char(to_date(:toDt),'yyyymm'))\r\n"
            + "group by mper.BI_ANL_KEY,to_char(mper.BI_ANL_STRT_DT,'MON-yyyy'),to_char(mper.BI_ANL_end_DT,'MON-yyyy'),ptyp.typ_seq,ptyp.typ_str,mjd.CRDT_DBT_FLG\r\n"
            + ")";
    public static final String TOP_SHEET = " select prdg.prd_grp_nm,emp.emp_nm,         sum(opn_clnt),\r\n"
            + "       sum(opn_prn_amt),         sum(opn_svc_amt),         sum(dsbmt_cnt),\r\n"
            + "       sum(dsbmt_prn_amt),         sum(dsbmt_svc_amt),   sum(rcvrd_prn_amt),\r\n"
            + "       sum(rcvrd_svc_amt),         sum(adj_clnt),         sum(adj_prn_amt),\r\n"
            + "       sum(adj_svc_amt),         sum(clsng_clnt),         sum(clsng_prn_amt),\r\n"
            + "       sum(clsng_svc_amt),         sum(cmpltd_loans)  from mw_brnch brnch\r\n"
            + "join mw_port prt on prt.brnch_seq=brnch.brnch_seq and prt.crnt_rec_flg=1\r\n"
            + "join mw_port_emp_rel erl on erl.port_seq=prt.port_seq and erl.crnt_rec_flg=1\r\n"
            + "join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n"
            + "join mw_brnch_prd_rel prl on prl.brnch_seq=brnch.brnch_seq and prl.crnt_rec_flg=1\r\n"
            + "join mw_prd prd on prd.prd_seq=prl.prd_seq and prd.crnt_rec_flg=1\r\n"
            + "join mw_prd_grp prdg on prdg.prd_grp_seq=prd.prd_grp_seq and prdg.crnt_rec_flg=1\r\n" + "left outer join \r\n" + "(  \r\n"
            + "select ap.prd_seq, ap.port_seq, count(ap.loan_app_seq) opn_clnt,\r\n"
            + "        sum(loan_app_ost(ap.loan_app_seq,to_date(:frmdt,'dd-MM-yyyy')-1,'s')) opn_svc_amt,\r\n"
            + "        sum(loan_app_ost(ap.loan_app_seq,to_date(:frmdt,'dd-MM-yyyy')-1,'p')) opn_prn_amt\r\n"
            + "        from mw_loan_app ap\r\n"
            + "        join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
            + "        where ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= to_date(:frmdt,'dd-MM-yyyy')-1 and ap.crnt_rec_flg=1)\r\n"
            + "        or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > to_date(:frmdt,'dd-MM-yyyy')-1)\r\n"
            + "        or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > to_date(:frmdt,'dd-MM-yyyy')-1))\r\n"
            + "        and loan_app_ost(ap.loan_app_seq,to_date(:frmdt,'dd-MM-yyyy')-1) > 0\r\n"
            + "        and not exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "        and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))\r\n"
            + "        group by ap.prd_seq,ap.port_seq                  \r\n"
            + ") ost on ost.prd_seq=prd.prd_seq and ost.port_seq=prt.port_seq\r\n" + "left outer join \r\n" + "( \r\n"
            + "select ap.prd_seq, ap.port_seq,\r\n" + "        count(ap.loan_app_seq) clsng_clnt,\r\n"
            + "        sum(loan_app_ost(ap.loan_app_seq,to_date(:todt,'dd-MM-yyyy'),'s')) clsng_svc_amt,\r\n"
            + "        sum(loan_app_ost(ap.loan_app_seq,to_date(:todt,'dd-MM-yyyy'),'p')) clsng_prn_amt\r\n"
            + "        from mw_loan_app ap\r\n"
            + "        join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
            + "        where ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= to_date(:todt,'dd-MM-yyyy') and ap.crnt_rec_flg=1)\r\n"
            + "        or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > to_date(:todt,'dd-MM-yyyy'))\r\n"
            + "        or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > to_date(:todt,'dd-MM-yyyy')))\r\n"
            + "        and loan_app_ost(ap.loan_app_seq,to_date(:todt,'dd-MM-yyyy')) > 0\r\n"
            + "        and not exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "        and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))\r\n"
            + "        group by ap.prd_seq,ap.port_seq        \r\n"
            + ") clsng on clsng.prd_seq=prd.prd_seq and clsng.port_seq=prt.port_seq\r\n" + "left outer join \r\n" + "(\r\n"
            + "    select ap.prd_seq,ap.port_seq,count(distinct ap.loan_app_seq) dsbmt_cnt,\r\n"
            + "           sum(ap.aprvd_loan_amt) dsbmt_prn_amt,             sum(svc.svc_chrg) dsbmt_svc_amt\r\n"
            + "    from mw_loan_app ap\r\n"
            + "    join mw_dsbmt_vchr_hdr hdr on hdr.loan_app_seq=ap.loan_app_seq and hdr.crnt_rec_flg=1\r\n"
            + "    join (select loan_app_seq,sum(tot_chrg_due) svc_chrg\r\n"
            + "              from mw_pymt_sched_hdr psh join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
            + "              where psh.crnt_rec_flg=1 group by psh.loan_app_seq) svc on svc.loan_app_seq=ap.loan_app_seq\r\n"
            + "    where ap.crnt_rec_flg=1\r\n"
            + "      and to_date(hdr.dsbmt_dt) between to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')\r\n"
            + "      and not exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "        and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))\r\n"
            + "    group by ap.prd_seq,ap.port_seq  \r\n" + " ) achvd on achvd.prd_seq=prd.prd_seq and achvd.port_seq=prt.port_seq\r\n"
            + "left outer join\r\n" + " (  \r\n" + "        select prd_seq, port_seq,\r\n"
            + "        count(distinct LOAN_APP_SEQ) rcvrd_clnt, \r\n" + "        sum(rec_pr) rcvrd_prn_amt, sum(rec_sc) rcvrd_svc_amt\r\n"
            + "        from\r\n" + "        (\r\n" + "        select ap.LOAN_APP_SEQ, ap.prd_seq, ap.port_seq,\r\n"
            + "                    nvl(sum((\r\n"
            + "                        select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_trx rht ,mw_rcvry_dtl rdtl\r\n"
            + "                        left outer join mw_typs rtyp on rtyp.typ_seq=rdtl.chrg_typ_key and rtyp.crnt_rec_flg=1             \r\n"
            + "                        where rdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg=1\r\n"
            + "                        and rht.RCVRY_TRX_SEQ = rdtl.RCVRY_TRX_SEQ and rht.crnt_rec_flg=1\r\n"
            + "                        and (rdtl.chrg_typ_key=-1)   \r\n"
            + "                        and rht.PYMT_DT BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')\r\n"
            + "                    )),0) rec_pr,\r\n" + "                    nvl(sum((\r\n"
            + "                        select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_trx rht ,mw_rcvry_dtl rdtl\r\n"
            + "                        left outer join mw_typs rtyp on rtyp.typ_seq=rdtl.chrg_typ_key and rtyp.crnt_rec_flg=1             \r\n"
            + "                        where rdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg=1\r\n"
            + "                        and rht.RCVRY_TRX_SEQ = rdtl.RCVRY_TRX_SEQ and rht.crnt_rec_flg=1\r\n"
            + "                        and (rdtl.chrg_typ_key in (select mt.typ_seq from mw_typs mt where mt.typ_id = '0017' and mt.crnt_rec_flg = 1))   \r\n"
            + "                        and rht.PYMT_DT BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')\r\n"
            + "                    )),0) rec_sc\r\n" + "                    --nto la, ost\r\n"
            + "                    from mw_loan_app ap\r\n"
            + "                    join mw_pymt_sched_hdr psh on psh.loan_app_seq=ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
            + "                    join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1,\r\n"
            + "                    mw_port mp\r\n" + "                    where ap.PORT_SEQ = mp.PORT_SEQ\r\n"
            + "                    and ap.crnt_rec_flg=1\r\n"
            + "                    and not exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "                    and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))                  \r\n"
            + "                    group by ap.prd_seq, ap.port_seq, ap.loan_app_seq\r\n" + "        )\r\n"
            + "        where (rec_pr > 0 or rec_sc > 0) \r\n" + "        group by prd_seq, port_seq       \r\n"
            + "    ) rcvd on rcvd.prd_seq=prd.prd_seq and rcvd.port_seq=prt.port_seq\r\n" + "        left outer join (   \r\n"
            + "        select prd_seq, PORT_SEQ, count(distinct loans) adj_clnt,\r\n" + "        sum(adj_pr) adj_prn_amt,\r\n"
            + "        sum(adj_sc) adj_svc_amt\r\n" + "        from (\r\n"
            + "        SELECT ap.prd_seq,  ap.loan_app_seq loans, ap.PORT_SEQ,\r\n"
            + "        (case when dtl.CHRG_TYP_KEY=-1 then nvl(SUM (distinct dtl.pymt_amt),0) else 0 end) adj_pr,\r\n"
            + "        (case when dtl.CHRG_TYP_KEY!=-1 then nvl(SUM (distinct dtl.pymt_amt),0) else 0 end) adj_sc\r\n"
            + "        FROM mw_loan_app ap join mw_port prt on prt.PORT_SEQ = ap.PORT_SEQ and prt.crnt_rec_flg = 1\r\n"
            + "        JOIN mw_ref_cd_val asts  ON asts.ref_cd_seq = ap.loan_app_sts  AND asts.crnt_rec_flg = 1 \r\n"
            + "        JOIN mw_pymt_sched_hdr psh  ON psh.loan_app_seq = ap.loan_app_seq  AND psh.crnt_rec_flg = 1 \r\n"
            + "        JOIN mw_pymt_sched_dtl psd  ON psd.pymt_sched_hdr_seq = psh.pymt_sched_hdr_seq  AND psd.crnt_rec_flg = 1 \r\n"
            + "        JOIN mw_ref_cd_val vl  ON vl.ref_cd_seq = psd.pymt_sts_key  AND vl.crnt_rec_flg = 1 and vl.REF_CD_GRP_KEY = 179 and vl.ref_cd = '0949'\r\n"
            + "        JOIN mw_rcvry_dtl dtl  ON (dtl.pymt_sched_dtl_seq is null or dtl.pymt_sched_dtl_seq = psd.PYMT_SCHED_DTL_SEQ)  AND dtl.crnt_rec_flg = 1 \r\n"
            + "        JOIN mw_rcvry_trx trx  ON trx.rcvry_trx_seq = dtl.rcvry_trx_seq  AND trx.crnt_rec_flg = 1 and trx.PYMT_REF = ap.CLNT_SEQ\r\n"
            + "        WHERE ap.crnt_rec_flg = 1 \r\n"
            + "        AND to_date(trx.pymt_dt) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')\r\n"
            + "        and not exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "        and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))\r\n"
            + "GROUP BY ap.prd_seq, dtl.CHRG_TYP_KEY, ap.loan_app_seq, ap.PORT_SEQ\r\n" + ")\r\n" + "group by prd_seq, PORT_SEQ        \r\n"
            + ") adj on adj.prd_seq=prd.prd_seq and adj.port_seq=prt.port_seq\r\n" + "left outer join (\r\n"
            + "     select ap.prd_seq,ap.port_seq,count(ap.loan_app_seq) cmpltd_loans\r\n" + "        from mw_loan_app ap\r\n"
            + "        join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
            + "        where ap.crnt_rec_flg=1\r\n"
            + "        and lsts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) between to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')\r\n"
            + "        and not exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "        and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))\r\n"
            + "        group by ap.prd_seq,ap.port_seq        \r\n"
            + "  ) cmpltd on cmpltd.prd_seq=prd.prd_seq and cmpltd.port_seq=prt.port_seq\r\n"
            + "where prd.prd_grp_seq=:prdseq  and brnch.brnch_seq=:brnchseq group by prdg.prd_grp_nm,emp.emp_nm\r\n" + "order by 1,2";
    public static final String PDC_DETAILS = "select distinct clnt.clnt_id,clnt.frst_nm||' '||clnt.last_nm clnt_nm,\r\n"
            + "    cbr.frst_nm||' '||cbr.last_nm pdc_prvdr_nm,\r\n" + "    bnk.ref_cd_dscr,\r\n" + "    phdr.acct_num,\r\n"
            + "    phdr.pdc_hdr_seq,dense_rank() over (PARTITION BY clnt.clnt_seq order by clnt.clnt_seq,chq_num) sr_no,\r\n"
            + "    chq_num\r\n" + "from mw_loan_app ap\r\n" + "join mw_port prt on prt.PORT_SEQ=ap.PORT_SEQ and prt.crnt_rec_flg=1\r\n"
            + "join mw_clnt clnt on clnt.clnt_seq=ap.clnt_seq and clnt.CRNT_REC_FLG=1\r\n"
            + "join mw_dsbmt_vchr_hdr dh on dh.loan_app_seq=ap.loan_app_seq and dh.crnt_rec_flg=1\r\n"
            + "join mw_clnt_rel cbr on cbr.loan_app_seq=ap.loan_app_seq and cbr.crnt_rec_flg=1 and cbr.rel_typ_flg=3\r\n"
            + "join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
            + "join mw_pdc_hdr phdr on phdr.loan_app_seq=ap.loan_app_seq and phdr.crnt_rec_flg=1\r\n"
            + "join mw_pdc_dtl pdtl on pdtl.pdc_hdr_seq=phdr.pdc_hdr_seq and pdtl.crnt_rec_flg=1\r\n"
            + "join mw_ref_cd_val bnk on bnk.ref_cd_seq=phdr.bank_key and asts.crnt_rec_flg=1\r\n" + "where ap.crnt_rec_flg=1\r\n"
            + "  and ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= to_date(:toDt,'dd-MM-yyyy') and ap.crnt_rec_flg=1)  \r\n"
            + "                    or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > to_date(:toDt,'dd-MM-yyyy'))\r\n"
            + "                    or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > to_date(:toDt,'dd-MM-yyyy')))\r\n"
            + "  and dh.dsbmt_dt between to_date(:frmDt,'dd-MM-yyyy') and to_date(:toDt,'dd-MM-yyyy') and prt.BRNCH_SEQ=:brnchSeq\r\n"
            + "order by clnt.clnt_id";
    public static final String PROJECTED_CLIENTS_LOANS_COMPLE = "select distinct emp.emp_nm bdo_nm, \r\n"
            + "clnt.clnt_id, clnt.frst_nm||' '||clnt.last_nm clnt_nm, \r\n"
            + "nvl(fthr_frst_nm,spz_frst_nm)||' '|| nvl(fthr_last_nm,spz_last_nm) fthr_spz_nm, \r\n" + "clnt.ph_num cntct_num, \r\n"
            + "(case when hse_num is not null then 'H No '||hse_num||', ' else null end)|| (case when strt is not null then 'St No '||strt||', ' else null end) || \r\n"
            + "(case when oth_dtl is not null then oth_dtl||', ' else null end) || \r\n"
            + "(case when vlg is not null then vlg||', ' else null end) || city_nm addr, \r\n"
            + "nvl(lu.ref_cd_dscr,lu.ref_cd_dscr) loan_usr, ap.loan_cycl_num, \r\n" + "ap.aprvd_loan_amt, cmp_dt, prd.prd_cmnt\r\n"
            + "from mw_loan_app ap\r\n" + "join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1\r\n"
            + "join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1 \r\n"
            + "join mw_port_emp_rel prl on prl.port_seq=prt.port_seq and prl.crnt_rec_flg=1 \r\n"
            + "join mw_emp emp on emp.emp_seq=prl.emp_seq \r\n"
            + "join mw_clnt clnt on clnt.clnt_seq=ap.clnt_seq and clnt.crnt_rec_flg=1 \r\n"
            + "join mw_addr_rel adrl on adrl.enty_key=clnt.clnt_seq and adrl.crnt_rec_flg=1 and adrl.enty_typ='Client' \r\n"
            + "join mw_addr adr on adr.addr_seq=adrl.addr_seq and adr.crnt_rec_flg=1 \r\n"
            + "join mw_city_uc_rel crel on crel.city_uc_rel_seq=adr.city_seq and crel.crnt_rec_flg=1 \r\n"
            + "join mw_city cty on cty.city_seq=crel.city_seq and cty.crnt_rec_flg=1 \r\n"
            + "left outer join (mw_biz_aprsl ba join mw_ref_cd_val lu on lu.ref_cd_seq=ba.prsn_run_the_biz and lu.crnt_rec_flg=1) on ba.loan_app_seq=ap.loan_app_seq and ba.crnt_rec_flg=1 \r\n"
            + "left outer join (mw_sch_aprsl sa join mw_ref_cd_val su on su.ref_cd_seq=sa.rel_wth_own_key and su.crnt_rec_flg=1) on sa.loan_app_seq=ap.loan_app_seq and ba.crnt_rec_flg=1 \r\n"
            + "join (select loan_app_seq,max(psd.due_dt) cmp_dt from mw_pymt_sched_hdr psh join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
            + "where psh.crnt_rec_flg=1 group by loan_app_seq) pymt on pymt.loan_app_seq=ap.loan_app_seq \r\n"
            + "where ap.crnt_rec_flg=1 and ap.loan_app_sts=703 \r\n"
            + "and cmp_dt between to_date(:frmDt,'dd-MM-yyyy') and to_date(:toDt,'dd-MM-yyyy') \r\n"
            + "and ap.prnt_loan_app_seq=ap.loan_app_seq\r\n" + "and prt.brnch_seq=:brnch order by bdo_nm,prd_cmnt desc,cmp_dt ";
    public static final String PAR_BRNACH_WISE = "select prd.PRD_NM,emp.EMP_NM,la.PORT_SEQ,\r\n"
            + "count(la.loan_app_seq) loan_app_seq ,\r\n" + "sum(nvl(la.APRVD_LOAN_AMT,0)) loan_amt,\r\n"
            + "sum(nvl(loan_app_ost(la.loan_app_seq,to_date(:toDt,'dd-MM-yyyy')),0)) ost_amt,\r\n"
            + "sum(nvl((nvl(od_op.PR_OD,0) + nvl(od_op.SC_OD,0)),0)) od_amt_op,\r\n" + "sum(0) addition,\r\n" + "sum(0) recovered,\r\n"
            + "sum(nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0)) od_amt_cl,\r\n" + "count(od_cl.loan_app_seq) od_loans_1,\r\n"
            + "sum((case when od_cl.loan_app_seq > 0 then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:toDt,'dd-MM-yyyy')),0) else 0 end)) ost_amt_1,\r\n"
            + "count((case when nvl(od_cl.OD_DYS,0) >= 30 then od_cl.loan_app_seq else null end)) od_loans_30,\r\n"
            + "sum((case when nvl(od_cl.OD_DYS,0) >= 30 then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:toDt,'dd-MM-yyyy')),0) else 0 end)) ost_amt_30,\r\n"
            + "count((case when (nvl(od_cl.OD_DYS,0) between 0 and 4 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then od_cl.loan_app_seq else null end)) od_loans_1_5,\r\n"
            + "sum((case when (nvl(od_cl.OD_DYS,0) between 0 and 4 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:toDt,'dd-MM-yyyy')),0) else 0 end)) ost_amt_1_5,\r\n"
            + "count((case when (nvl(od_cl.OD_DYS,0) between 5 and 9 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then od_cl.loan_app_seq else null end)) od_loans_6_10,\r\n"
            + "sum((case when (nvl(od_cl.OD_DYS,0) between 5 and 9 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:toDt,'dd-MM-yyyy')),0) else 0 end)) ost_amt_6_10,\r\n"
            + "count((case when (nvl(od_cl.OD_DYS,0) between 10 and 14 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then od_cl.loan_app_seq else null end)) od_loans_11_15,\r\n"
            + "sum((case when (nvl(od_cl.OD_DYS,0) between 10 and 14 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:toDt,'dd-MM-yyyy')),0) else 0 end)) ost_amt_11_15,\r\n"
            + "count((case when (nvl(od_cl.OD_DYS,0) between 15 and 29 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then od_cl.loan_app_seq else null end)) od_loans_16_30,\r\n"
            + "sum((case when (nvl(od_cl.OD_DYS,0) between 15 and 29 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:toDt,'dd-MM-yyyy')),0) else 0 end)) ost_amt_16_30,\r\n"
            + "count((case when (nvl(od_cl.OD_DYS,0) between 30 and 89 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then od_cl.loan_app_seq else null end)) od_loans_31_90,\r\n"
            + "sum((case when (nvl(od_cl.OD_DYS,0) between 30 and 89 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:toDt,'dd-MM-yyyy')),0) else 0 end)) ost_amt_31_90,\r\n"
            + "count((case when (nvl(od_cl.OD_DYS,0) between 90 and 179 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then od_cl.loan_app_seq else null end)) od_loans_90_180,\r\n"
            + "sum((case when (nvl(od_cl.OD_DYS,0) between 90 and 179 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:toDt,'dd-MM-yyyy')),0) else 0 end)) ost_amt_91_180,\r\n"
            + "count((case when (nvl(od_cl.OD_DYS,0) between 180 and 364 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then od_cl.loan_app_seq else null end)) od_loans_181_365,\r\n"
            + "sum((case when (nvl(od_cl.OD_DYS,0) between 180 and 364 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:toDt,'dd-MM-yyyy')),0) else 0 end)) ost_amt_181_365,\r\n"
            + "count((case when (nvl(od_cl.OD_DYS,0) >= 365 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then od_cl.loan_app_seq else null end)) od_loans_365,\r\n"
            + "sum((case when (nvl(od_cl.OD_DYS,0) >= 365 and nvl((nvl(od_cl.PR_OD,0) + nvl(od_cl.SC_OD,0)),0) > 0) then nvl(loan_app_ost(od_cl.loan_app_seq,to_date(:toDt,'dd-MM-yyyy')),0) else 0 end)) ost_amt_365,\r\n"
            + "sum(nvl(od_cl.OD_DYS,0)) od_dys \r\n" + "from mw_loan_app la,mw_prd prd,mw_port_emp_rel per,mw_emp emp,mw_port prt,\r\n"
            + "mw_ref_cd_val asts, \r\n" + "(\r\n" + "select shld_rec.LOAN_APP_SEQ,\r\n" + "pr_due - pr_rec pr_od,\r\n"
            + "sc_due - sc_rec sc_od,\r\n" + "chrg_due - chrg_rec chrg_od,\r\n" + "nvl(to_date(:toDt,'dd-MM-yyyy') - od_dt,0) od_dys \r\n"
            + "from \r\n" + "(\r\n" + "select ap.LOAN_APP_SEQ, nvl(sum(psd.ppal_amt_due),0) pr_due, nvl(sum(tot_chrg_due),0) sc_due, \r\n"
            + "nvl(sum((select sum(amt) from mw_pymt_sched_chrg psc where psc.PYMT_SCHED_DTL_SEQ = psd.PYMT_SCHED_DTL_SEQ and crnt_rec_flg=1)),0) chrg_due,\r\n"
            + "max(INST_NUM) inst_num, min(psd.due_dt) od_dt\r\n" + "from mw_loan_app ap\r\n"
            + "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
            + "join mw_pymt_sched_hdr psh on psh.loan_app_seq= ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
            + "join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1\r\n"
            + "and psd.due_dt <= to_date(:toDt,'dd-MM-yyyy') \r\n"
            + "and (psd.PYMT_STS_KEY in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945','1145') and REF_CD_GRP_KEY = 179 and val.crnt_rec_flg=1)\r\n"
            + "or (psd.PYMT_STS_KEY = (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948') and REF_CD_GRP_KEY = 179 and val.crnt_rec_flg=1)\r\n"
            + "and (\r\n" + "select max(trx.pymt_dt) \r\n"
            + "from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n"
            + "and rdtl.PYMT_SCHED_DTL_SEQ = psd.PYMT_SCHED_DTL_SEQ) > to_date(:toDt,'dd-MM-yyyy')\r\n" + ")\r\n" + ")\r\n"
            + "and ap.crnt_rec_flg =1\r\n"
            + "and ap.LOAN_APP_STS not in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('1245') and REF_CD_GRP_KEY = 106 and val.crnt_rec_flg=1)\r\n"
            + "group by ap.LOAN_APP_SEQ\r\n" + ")shld_rec,\r\n" + "(\r\n" + "select ap.loan_app_seq,\r\n" + "sum(nvl(pr_rec,0)) pr_rec,\r\n"
            + "sum(nvl(sc_rec,0)) sc_rec,\r\n" + "sum(nvl(chrg_rec,0)) chrg_rec\r\n" + "from mw_loan_app ap\r\n"
            + "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
            + "join mw_pymt_sched_hdr psh on psh.loan_app_seq= ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
            + "join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1 \r\n" + "left outer join (\r\n"
            + "select rdtl.pymt_sched_dtl_seq , pymt_dt , \r\n"
            + "(case when CHRG_TYP_KEY = -1 then nvl(sum(nvl(rdtl.pymt_amt,0)),0) end) pr_rec,\r\n"
            + "(case when CHRG_TYP_KEY in (416,413,418,419,383,414,17,415,417,412,410,411) then nvl(sum(nvl(rdtl.pymt_amt,0)),0) end) sc_rec,\r\n"
            + "(case when CHRG_TYP_KEY not in (-1,416,413,418,419,383,414,17,415,417,412,410,411) then nvl(sum(nvl(rdtl.pymt_amt,0)),0) end) chrg_rec\r\n"
            + "from mw_pymt_sched_dtl psd\r\n"
            + "join mw_rcvry_dtl rdtl on rdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg=1\r\n"
            + "join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n" + "where psd.crnt_rec_flg=1\r\n"
            + "and trx.PYMT_DT <= to_date(:toDt,'dd-MM-yyyy')\r\n"
            + "group by rdtl.pymt_sched_dtl_seq,pymt_dt,CHRG_TYP_KEY) pDt on pdt.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq\r\n"
            + "where ap.crnt_rec_flg=1\r\n" + "and psd.due_dt <= to_date(:toDt,'dd-MM-yyyy')\r\n"
            + "and (psd.PYMT_STS_KEY in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945','1145') and REF_CD_GRP_KEY = 179 and val.crnt_rec_flg=1)\r\n"
            + "or (psd.PYMT_STS_KEY = (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948') and REF_CD_GRP_KEY = 179 and val.crnt_rec_flg=1)\r\n"
            + "and (\r\n" + "select max(trx.pymt_dt) \r\n"
            + "from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n"
            + "and rdtl.PYMT_SCHED_DTL_SEQ = psd.PYMT_SCHED_DTL_SEQ) > to_date(:toDt,'dd-MM-yyyy')\r\n" + ")\r\n" + ")\r\n"
            + "and ap.crnt_rec_flg =1\r\n"
            + "and ap.LOAN_APP_STS not in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('1245') and REF_CD_GRP_KEY = 106 and val.crnt_rec_flg=1)\r\n"
            + "group by ap.loan_app_seq\r\n" + ") actl_rec\r\n" + "where shld_rec.loan_app_seq = actl_rec.loan_app_seq(+)\r\n"
            + "and ((pr_due - pr_rec) > 0 or (sc_due - sc_rec) > 0 or (chrg_due - chrg_rec) > 0)\r\n" + ") od_cl,\r\n" + "(\r\n"
            + "select shld_rec.LOAN_APP_SEQ,\r\n" + "pr_due - pr_rec pr_od,\r\n" + "sc_due - sc_rec sc_od,\r\n"
            + "chrg_due - chrg_rec chrg_od,\r\n" + "TO_DATE (to_date(:fromDt,'dd-MM-yyyy')-1) - od_dt od_dys \r\n" + "from \r\n" + "(\r\n"
            + "select ap.LOAN_APP_SEQ, nvl(sum(psd.ppal_amt_due),0) pr_due, nvl(sum(tot_chrg_due),0) sc_due, \r\n"
            + "nvl(sum((select sum(amt) from mw_pymt_sched_chrg psc where psc.PYMT_SCHED_DTL_SEQ = psd.PYMT_SCHED_DTL_SEQ and crnt_rec_flg=1)),0) chrg_due,\r\n"
            + "max(INST_NUM) inst_num, min(psd.due_dt) od_dt\r\n" + "from mw_loan_app ap\r\n"
            + "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
            + "join mw_pymt_sched_hdr psh on psh.loan_app_seq= ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
            + "join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1\r\n"
            + "and psd.due_dt <= to_date(:fromDt,'dd-MM-yyyy')-1\r\n"
            + "and (psd.PYMT_STS_KEY in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945','1145') and REF_CD_GRP_KEY = 179 and val.crnt_rec_flg=1)\r\n"
            + "or (psd.PYMT_STS_KEY = (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948') and REF_CD_GRP_KEY = 179 and val.crnt_rec_flg=1)\r\n"
            + "and (\r\n" + "select max(trx.pymt_dt) \r\n"
            + "from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n"
            + "and rdtl.PYMT_SCHED_DTL_SEQ = psd.PYMT_SCHED_DTL_SEQ) > to_date(:fromDt,'dd-MM-yyyy')-1\r\n" + ")\r\n" + ")\r\n"
            + "and ap.crnt_rec_flg =1\r\n"
            + "and ap.LOAN_APP_STS not in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('1245') and REF_CD_GRP_KEY = 106 and val.crnt_rec_flg=1)\r\n"
            + "group by ap.LOAN_APP_SEQ\r\n" + ")shld_rec,\r\n" + "(\r\n" + "select ap.loan_app_seq,\r\n" + "sum(nvl(pr_rec,0)) pr_rec,\r\n"
            + "sum(nvl(sc_rec,0)) sc_rec,\r\n" + "sum(nvl(chrg_rec,0)) chrg_rec\r\n" + "from mw_loan_app ap\r\n"
            + "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
            + "join mw_pymt_sched_hdr psh on psh.loan_app_seq= ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
            + "join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1 \r\n" + "left outer join (\r\n"
            + "select rdtl.pymt_sched_dtl_seq , pymt_dt , \r\n"
            + "(case when CHRG_TYP_KEY = -1 then nvl(sum(nvl(rdtl.pymt_amt,0)),0) end) pr_rec,\r\n"
            + "(case when CHRG_TYP_KEY in (416,413,418,419,383,414,17,415,417,412,410,411) then nvl(sum(nvl(rdtl.pymt_amt,0)),0) end) sc_rec,\r\n"
            + "(case when CHRG_TYP_KEY not in (-1,416,413,418,419,383,414,17,415,417,412,410,411) then nvl(sum(nvl(rdtl.pymt_amt,0)),0) end) chrg_rec\r\n"
            + "from mw_pymt_sched_dtl psd\r\n"
            + "join mw_rcvry_dtl rdtl on rdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg=1\r\n"
            + "join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n" + "where psd.crnt_rec_flg=1\r\n"
            + "and trx.PYMT_DT <= to_date(:fromDt,'dd-MM-yyyy')-1\r\n"
            + "group by rdtl.pymt_sched_dtl_seq,pymt_dt,CHRG_TYP_KEY) pDt on pdt.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq\r\n"
            + "where ap.crnt_rec_flg=1\r\n" + "and psd.due_dt <= to_date(:fromDt,'dd-MM-yyyy')-1\r\n"
            + "and (psd.PYMT_STS_KEY in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945','1145') and REF_CD_GRP_KEY = 179 and val.crnt_rec_flg=1)\r\n"
            + "or (psd.PYMT_STS_KEY = (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948') and REF_CD_GRP_KEY = 179 and val.crnt_rec_flg=1)\r\n"
            + "and (\r\n" + "select max(trx.pymt_dt) \r\n"
            + "from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1\r\n"
            + "and rdtl.PYMT_SCHED_DTL_SEQ = psd.PYMT_SCHED_DTL_SEQ) > to_date(:fromDt,'dd-MM-yyyy')-1\r\n" + ")\r\n" + ")\r\n"
            + "and ap.crnt_rec_flg =1\r\n"
            + "and ap.LOAN_APP_STS not in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('1245') and REF_CD_GRP_KEY = 106 and val.crnt_rec_flg=1)\r\n"
            + "group by ap.loan_app_seq\r\n" + ") actl_rec\r\n" + "where shld_rec.loan_app_seq = actl_rec.loan_app_seq(+)\r\n"
            + "and ((pr_due - pr_rec) > 0 or (sc_due - sc_rec) > 0 or (chrg_due - chrg_rec) > 0)\r\n" + ") od_op\r\n"
            + "where asts.ref_cd_seq=la.loan_app_sts \r\n" + "and asts.crnt_rec_flg=1\r\n"
            + "and la.LOAN_APP_SEQ = od_cl.LOAN_APP_SEQ (+)\r\n" + "and la.LOAN_APP_SEQ = od_op.LOAN_APP_SEQ (+)\r\n"
            + "and la.PORT_SEQ = prt.PORT_SEQ and prt.CRNT_REC_FLG = 1 and prt.BRNCH_SEQ=:brnch\r\n"
            + "and la.PORT_SEQ = per.PORT_SEQ and per.CRNT_REC_FLG = 1\r\n" + "and per.EMP_SEQ = emp.EMP_SEQ\r\n"
            + "and la.PRD_SEQ =prd.PRD_SEQ and prd.CRNT_REC_FLG = 1\r\n"
            + "and ((asts.ref_cd='0005' and to_date(la.loan_app_sts_dt) <= to_date(:toDt,'dd-MM-yyyy') and la.crnt_rec_flg=1) --toDate\r\n"
            + "or (asts.ref_cd='0006' and to_date(la.loan_app_sts_dt) > to_date(:toDt,'dd-MM-yyyy'))--toDate\r\n"
            + "or (asts.ref_cd='1245' and to_date(la.loan_app_sts_dt) > to_date(:toDt,'dd-MM-yyyy'))) \r\n" + "and la.crnt_rec_flg =1\r\n"
            + "and loan_app_ost(la.loan_app_seq,to_date(:toDt,'dd-MM-yyyy')) > 0\r\n" + "group by prd.PRD_NM,emp.EMP_NM,la.PORT_SEQ\r\n"
            + "order by prd.PRD_NM,emp.EMP_NM";
    public static final String TAG_TOPSHEET = "select prdg.prd_grp_nm,emp.emp_nm,         sum(opn_clnt),\r\n"
            + "                               sum(opn_prn_amt),         sum(opn_svc_amt),         sum(dsbmt_cnt),\r\n"
            + "                               sum(dsbmt_prn_amt),         sum(dsbmt_svc_amt),   sum(rcvrd_prn_amt),\r\n"
            + "                               sum(rcvrd_svc_amt),         sum(adj_clnt),         sum(adj_prn_amt),\r\n"
            + "                               sum(adj_svc_amt),         sum(clsng_clnt),         sum(clsng_prn_amt),\r\n"
            + "                               sum(clsng_svc_amt),         sum(cmpltd_loans)  from mw_brnch brnch\r\n"
            + "                        join mw_port prt on prt.brnch_seq=brnch.brnch_seq and prt.crnt_rec_flg=1\r\n"
            + "                        join mw_port_emp_rel erl on erl.port_seq=prt.port_seq and erl.crnt_rec_flg=1\r\n"
            + "                        join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n"
            + "                        join mw_brnch_prd_rel prl on prl.brnch_seq=brnch.brnch_seq and prl.crnt_rec_flg=0 and prl.del_flg=1\r\n"
            + "                        join mw_prd prd on prd.prd_seq=prl.prd_seq and prd.crnt_rec_flg=1\r\n"
            + "                        join mw_prd_grp prdg on prdg.prd_grp_seq=prd.prd_grp_seq and prdg.crnt_rec_flg=1 left outer join  (  \r\n"
            + "                        select ap.prd_seq, ap.port_seq, count(ap.loan_app_seq) opn_clnt,\r\n"
            + "                                sum(loan_app_ost(ap.loan_app_seq,to_date(:frmdt,'dd-MM-yyyy')-1,'s')) opn_svc_amt,\r\n"
            + "                                sum(loan_app_ost(ap.loan_app_seq,to_date(:frmdt,'dd-MM-yyyy')-1,'p')) opn_prn_amt\r\n"
            + "                                from mw_loan_app ap\r\n"
            + "                                join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
            + "                                join mw_port prt on ap.port_seq=prt.port_seq and prt.crnt_rec_flg=1\r\n"
            + "                                where ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= to_date(:frmdt,'dd-MM-yyyy')-1 and ap.crnt_rec_flg=1)\r\n"
            + "                                or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > to_date(:frmdt,'dd-MM-yyyy')-1)\r\n"
            + "                                or (asts.ref_cd='1245'))\r\n"
            + "                                and loan_app_ost(ap.loan_app_seq,to_date(:frmdt,'dd-MM-yyyy')-1) > 0\r\n"
            + "                                and exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "                                and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))\r\n"
            + "                                and prt.brnch_seq=:brnchseq                     group by ap.prd_seq,ap.port_seq\r\n"
            + "                        ) ost on ost.prd_seq=prd.prd_seq and ost.port_seq=prt.port_seq left outer join  ( \r\n"
            + "                        select ap.prd_seq, ap.port_seq,         count(ap.loan_app_seq) clsng_clnt,\r\n"
            + "                                sum(loan_app_ost(ap.loan_app_seq,to_date(:todt,'dd-MM-yyyy'),'s')) clsng_svc_amt,\r\n"
            + "                                sum(loan_app_ost(ap.loan_app_seq,to_date(:todt,'dd-MM-yyyy'),'p')) clsng_prn_amt\r\n"
            + "                                from mw_loan_app ap\r\n"
            + "                                join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1\r\n"
            + "                                where ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= to_date(:todt,'dd-MM-yyyy') and ap.crnt_rec_flg=1)\r\n"
            + "                                or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > to_date(:todt,'dd-MM-yyyy'))\r\n"
            + "                                or (asts.ref_cd='1245'))\r\n"
            + "                                and loan_app_ost(ap.loan_app_seq,to_date(:todt,'dd-MM-yyyy')) > 0\r\n"
            + "                                and exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "                                and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))\r\n"
            + "                                group by ap.prd_seq,ap.port_seq \r\n"
            + "                        ) clsng on clsng.prd_seq=prd.prd_seq and clsng.port_seq=prt.port_seq left outer join  (\r\n"
            + "                            select ap.prd_seq,ap.port_seq,count(distinct ap.loan_app_seq) dsbmt_cnt,\r\n"
            + "                                   sum(ap.aprvd_loan_amt) dsbmt_prn_amt,             sum(svc.svc_chrg) dsbmt_svc_amt\r\n"
            + "                            from mw_loan_app ap\r\n"
            + "                            join mw_dsbmt_vchr_hdr hdr on hdr.loan_app_seq=ap.loan_app_seq and hdr.crnt_rec_flg=1\r\n"
            + "                            join (select loan_app_seq,sum(tot_chrg_due) svc_chrg\r\n"
            + "                                      from mw_pymt_sched_hdr psh join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
            + "                                      where psh.crnt_rec_flg=1 group by psh.loan_app_seq) svc on svc.loan_app_seq=ap.loan_app_seq\r\n"
            + "                            where ap.crnt_rec_flg=1\r\n"
            + "                              and to_date(hdr.dsbmt_dt) between to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')\r\n"
            + "                              and exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "                                and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))\r\n"
            + "                            group by ap.prd_seq,ap.port_seq    ) achvd on achvd.prd_seq=prd.prd_seq and achvd.port_seq=prt.port_seq\r\n"
            + "                        left outer join  (           select prd_seq, port_seq,\r\n"
            + "                                count(distinct LOAN_APP_SEQ) rcvrd_clnt,          sum(rec_pr) rcvrd_prn_amt, sum(rec_sc) rcvrd_svc_amt\r\n"
            + "                                from         (         select ap.LOAN_APP_SEQ, ap.prd_seq, ap.port_seq,\r\n"
            + "                                            nvl(sum((\r\n"
            + "                                                select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_trx rht ,mw_rcvry_dtl rdtl\r\n"
            + "                                                left outer join mw_typs rtyp on rtyp.typ_seq=rdtl.chrg_typ_key and rtyp.crnt_rec_flg=1             \r\n"
            + "                                                where rdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg=1\r\n"
            + "                                                and rht.RCVRY_TRX_SEQ = rdtl.RCVRY_TRX_SEQ and rht.crnt_rec_flg=1\r\n"
            + "                                                and (rdtl.chrg_typ_key=-1)   \r\n"
            + "                                                and to_date(rht.PYMT_DT) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')\r\n"
            + "                                            )),0) rec_pr,                     nvl(sum((\r\n"
            + "                                                select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_trx rht ,mw_rcvry_dtl rdtl\r\n"
            + "                                                left outer join mw_typs rtyp on rtyp.typ_seq=rdtl.chrg_typ_key and rtyp.crnt_rec_flg=1             \r\n"
            + "                                                where rdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg=1\r\n"
            + "                                                and rht.RCVRY_TRX_SEQ = rdtl.RCVRY_TRX_SEQ and rht.crnt_rec_flg=1\r\n"
            + "                                                and (rdtl.chrg_typ_key in (select mt.typ_seq from mw_typs mt where mt.typ_id = '0017' and mt.crnt_rec_flg = 1))   \r\n"
            + "                                                and to_date(rht.PYMT_DT) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')\r\n"
            + "                                            )),0) rec_sc                     --nto la, ost\r\n"
            + "                                            from mw_loan_app ap\r\n"
            + "                                            join mw_pymt_sched_hdr psh on psh.loan_app_seq=ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
            + "                                            join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1,\r\n"
            + "                                            mw_port mp                     where ap.PORT_SEQ = mp.PORT_SEQ\r\n"
            + "                                            and ap.crnt_rec_flg=1 and psd.PYMT_STS_KEY not in (949) and mp.brnch_seq = :brnchseq\r\n"
            + "                                            and exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "                                            and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))                  \r\n"
            + "                                            group by ap.prd_seq, ap.port_seq, ap.loan_app_seq         )\r\n"
            + "                                where (rec_pr > 0 or rec_sc > 0)          group by prd_seq, port_seq       \r\n"
            + "                            ) rcvd on rcvd.prd_seq=prd.prd_seq and rcvd.port_seq=prt.port_seq         left outer join (   \r\n"
            + "                                select prd_seq, PORT_SEQ, count(distinct loans) adj_clnt,         sum(adj_pr) adj_prn_amt,\r\n"
            + "                                sum(adj_sc) adj_svc_amt         from (\r\n"
            + "                                SELECT ap.prd_seq,  ap.loan_app_seq loans, ap.PORT_SEQ,\r\n"
            + "                                (case when dtl.CHRG_TYP_KEY=-1 then nvl(SUM (distinct dtl.pymt_amt),0) else 0 end) adj_pr,\r\n"
            + "                                (case when (dtl.chrg_typ_key in (select mt1.typ_seq from mw_typs mt1 where mt1.typ_id = '0017' and mt1.crnt_rec_flg = 1)) then nvl(SUM (distinct dtl.pymt_amt),0) else 0 end) adj_sc \r\n"
            + "                                FROM mw_loan_app ap join mw_port prt on prt.PORT_SEQ = ap.PORT_SEQ and prt.crnt_rec_flg = 1\r\n"
            + "                                JOIN mw_ref_cd_val asts  ON asts.ref_cd_seq = ap.loan_app_sts  AND asts.crnt_rec_flg = 1 \r\n"
            + "                                JOIN mw_pymt_sched_hdr psh  ON psh.loan_app_seq = ap.loan_app_seq  AND psh.crnt_rec_flg = 1 \r\n"
            + "                                JOIN mw_pymt_sched_dtl psd  ON psd.pymt_sched_hdr_seq = psh.pymt_sched_hdr_seq  AND psd.crnt_rec_flg = 1 \r\n"
            + "                                JOIN mw_ref_cd_val vl  ON vl.ref_cd_seq = psd.pymt_sts_key  AND vl.crnt_rec_flg = 1 and vl.REF_CD_GRP_KEY = 179 and vl.ref_cd = '0949'\r\n"
            + "                                JOIN mw_rcvry_dtl dtl  ON (dtl.pymt_sched_dtl_seq is null or dtl.pymt_sched_dtl_seq = psd.PYMT_SCHED_DTL_SEQ)  AND dtl.crnt_rec_flg = 1 \r\n"
            + "                                JOIN mw_rcvry_trx trx  ON trx.rcvry_trx_seq = dtl.rcvry_trx_seq  AND trx.crnt_rec_flg = 1 and trx.PYMT_REF = ap.CLNT_SEQ\r\n"
            + "                                WHERE ap.crnt_rec_flg = 1 \r\n"
            + "                                AND to_date(trx.pymt_dt) BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy') \r\n"
            + "                                and prt.brnch_seq = :brnchseq  \r\n"
            + "                                and (dtl.CHRG_TYP_KEY=-1 or (dtl.chrg_typ_key in (select mt1.typ_seq from mw_typs mt1 where mt1.typ_id = '0017' and mt1.crnt_rec_flg = 1)))\r\n"
            + "                                and exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "                                and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))\r\n"
            + "                        GROUP BY ap.prd_seq, dtl.CHRG_TYP_KEY, ap.loan_app_seq, ap.PORT_SEQ ) group by prd_seq, PORT_SEQ        \r\n"
            + "                        ) adj on adj.prd_seq=prd.prd_seq and adj.port_seq=prt.port_seq left outer join (\r\n"
            + "                             select ap.prd_seq,ap.port_seq,count(ap.loan_app_seq) cmpltd_loans         from mw_loan_app ap\r\n"
            + "                                join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1\r\n"
            + "                                where ap.crnt_rec_flg=1\r\n"
            + "                                and lsts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) between to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')\r\n"
            + "                                and exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "                                and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))\r\n"
            + "                                group by ap.prd_seq,ap.port_seq        \r\n"
            + "                          ) cmpltd on cmpltd.prd_seq=prd.prd_seq and cmpltd.port_seq=prt.port_seq\r\n"
            + "                        where prd.prd_grp_seq=:prdseq  and brnch.brnch_seq=:brnchseq group by prdg.prd_grp_nm,emp.emp_nm order by 1,2\r\n"
            + "";
    public static final String TAGGED_CLIENT_CLAIM = "select 'No_Active_Portfolio' emp_nm, \r\n" + "        AP.CLNT_SEQ clsng_clnt, \r\n"
            + "        get_clnt_SPZ_NM(ap.loan_app_seq) clsng_clnt_NAME,\r\n"
            + "        (select distinct CTL.TAG_FROM_DT from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "        and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:aDt,'dd-MM-yyyy')) TAG_DT,\r\n"
            + "        C.PH_NUM,\r\n" + "        get_clnt_addr(ap.loan_app_seq) ADDRESS,\r\n" + "        DSBH.DSBMT_DT,\r\n"
            + "        DSBD.AMT DISB_AMT,\r\n" + "        loan_app_ost(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'ri') clsng_rem_inst,\r\n"
            + "        loan_app_ost(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'ps') clsng_OST_AMT,\r\n"
            + "        loan_app_ost(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'ri') clsng_od_rem_inst,\r\n"
            + "        loan_app_ost(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'ps') clsng_od_AMT,\r\n"
            + "        get_od_info(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'d') od_days,       \r\n"
            + "        ((select distinct CTL.TAG_FROM_DT from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:aDt,'dd-MM-yyyy')) - (to_date(:aDt,'dd-MM-yyyy') - get_od_info(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'d'))) od_days_when_taged\r\n"
            + "  FROM MW_LOAN_APP ap,\r\n" + "       MW_CLNT C,\r\n" + "       MW_PRD MP,\r\n" + "       MW_PORT MP1,\r\n"
            + "       MW_PRD_GRP MPG,\r\n" + "       MW_DSBMT_VCHR_HDR DSBH,\r\n" + "       MW_DSBMT_VCHR_DTL DSBD\r\n"
            + "   where ap.PRD_SEQ = MP.PRD_SEQ\r\n" + "AND MP.PRD_GRP_SEQ = MPG.PRD_GRP_SEQ \r\n" + "and MPG.PRD_GRP_SEQ = :prdSeq\r\n"
            + "AND MPG.CRNT_REC_FLG = 1\r\n" + "AND MP.CRNT_REC_FLG = 1\r\n" + "AND ap.CRNT_REC_FLG = 1\r\n"
            + "AND ap.CLNT_SEQ = C.CLNT_SEQ\r\n" + "AND C.CRNT_REC_FLG = 1\r\n" + "AND ap.LOAN_APP_SEQ = DSBH.LOAN_APP_SEQ\r\n"
            + "AND DSBH.CRNT_REC_FLG = 1\r\n" + "AND DSBH.DSBMT_HDR_SEQ = DSBD.DSBMT_HDR_SEQ\r\n" + "AND DSBD.CRNT_REC_FLG = 1\r\n"
            + "AND ap.PORT_SEQ = MP1.PORT_SEQ\r\n" + "AND MP1.CRNT_REC_FLG = 1\r\n" + "and mp1.brnch_seq = :brnchSeq\r\n"
            + "and exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(to_date(:aDt,'dd-MM-yyyy')))\r\n"
            + "and loan_app_ost(ap.loan_app_seq,to_date(to_date(:aDt,'dd-MM-yyyy'))) > 0";
    public static final String TRANSFERRED_CLIENTS = "select \r\n" + "prd.prd_nm, clnt.clnt_id, \r\n"
            + "ap.loan_app_seq,clnt.frst_nm||' '||clnt.last_nm clnt_nm, \r\n"
            + "(select emp_nm from mw_emp emp join mw_port_emp_rel pr on pr.emp_seq=emp.emp_seq and pr.crnt_rec_flg=1 and pr.port_seq=trns.from_port) frm_bdo, \r\n"
            + "(select emp_nm from mw_emp emp join mw_port_emp_rel pr on pr.emp_seq=emp.emp_seq and pr.crnt_rec_flg=1 and pr.port_seq=trns.to_port) to_bdo, \r\n"
            + "(select brnch_nm from mw_brnch brnch join mw_port prt on prt.brnch_seq=brnch.brnch_seq and prt.crnt_rec_flg=1 \r\n"
            + "                 and prt.port_seq=trns.from_port where brnch.crnt_rec_flg=1) from_branch,\r\n"
            + "(select brnch_nm from mw_brnch brnch join mw_port prt on prt.brnch_seq=brnch.brnch_seq and prt.crnt_rec_flg=1 \r\n"
            + "                 and prt.port_seq=trns.to_port where brnch.crnt_rec_flg=1) to_branch,\r\n"
            + "ap.loan_app_sts_dt, ap.aprvd_loan_amt, \r\n" + "(select count(psd.pymt_sched_dtl_seq) \r\n"
            + "    from mw_pymt_sched_hdr psh join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
            + "    where psh.crnt_rec_flg=1 and psd.pymt_sts_key=945 and psh.loan_app_seq=ap.loan_app_seq) inst_num, \r\n"
            + "loan_app_ost(ap.loan_app_seq,trns.trns_dt,'p') ost_pr, \r\n"
            + "loan_app_ost(ap.loan_app_seq,trns.trns_dt,'s') ost_sc, 0 KSZB_ost, 0 doc_fee_ost, \r\n" + "0 trn_ost, \r\n"
            + "0 ins_ost, \r\n" + "0 ex_rcvry \r\n" + "from mw_loan_app ap \r\n"
            + "join mw_loan_app_trns trns on trns.loan_app_seq=ap.loan_app_seq and trns.crnt_rec_flg=1\r\n"
            + "join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
            + "join mw_clnt clnt on clnt.clnt_seq=ap.clnt_seq and clnt.crnt_rec_flg=1 \r\n"
            + "join ( select reg_nm, area_nm, brnch_nm, prt.port_seq \r\n"
            + "    from mw_reg rg join mw_area ar on ar.reg_seq=rg.reg_seq and ar.crnt_rec_flg=1 \r\n"
            + "    join mw_brnch br on br.area_seq=ar.area_seq and br.crnt_rec_flg=1 \r\n"
            + "    join mw_port prt on prt.brnch_seq=br.brnch_seq and prt.crnt_rec_flg=1 where rg.crnt_rec_flg=1 \r\n"
            + ") fi on fi.port_seq=trns.from_port \r\n" + "where ap.crnt_rec_flg=1\r\n"
            + "and to_date(trns.trns_dt) between to_date(:frmDt,'dd-MM-yyyy') and to_date(:toDt,'dd-MM-yyyy') order by 1";
    public static String loanInfoByLoanAppSeq = "select app.loan_id, (select ref_cd from mw_ref_cd_val where ref_cd_seq=app.loan_app_sts and crnt_rec_flg=1)\r\n"
            + "as loan_app_sts, clnt.frst_nm, clnt.last_nm, clnt.clnt_id, clnt.cnic_num, \r\n"
            + "(select ref_cd_dscr from mw_ref_cd_val where ref_cd_seq=clnt.gndr_key and crnt_rec_flg=1) as gender_key, \r\n"
            + "(select ref_cd_dscr from mw_ref_cd_val where ref_cd_seq=clnt.mrtl_sts_key and crnt_rec_flg=1) as marital_sts, \r\n"
            + "ad.hse_num, cit.city_nm,uc.uc_nm, thsl.thsl_nm, dist.dist_nm, st.st_nm, cntry.cntry_nm, \r\n"
            + "port.port_cd,port.port_nm,brnch.brnch_cd,brnch.brnch_nm, area.area_nm, reg.reg_nm , prd.prd_cmnt,prd.prd_id, app.aprvd_loan_amt,psh.frst_inst_dt,\r\n"
            + "acty.biz_acty_nm||'/ '||ba.biz_dtl_str acty,sect.biz_sect_nm sect,emp.emp_nm bdo,psh.pymt_sched_hdr_seq,clnt.spz_frst_nm||clnt.fthr_frst_nm,clnt.spz_last_nm||clnt.fthr_last_nm,slb.val,pc1.chrg_val doc,app.loan_cycl_num, \r\n"
            + "(select acty.biz_acty_nm||'/ '||ba.biz_dtl_str\r\n" + "from mw_loan_app l \r\n"
            + "left outer join mw_biz_aprsl ba on ba.loan_app_seq=l.loan_app_seq and ba.crnt_rec_flg=1\r\n"
            + "left outer join mw_biz_acty acty on ba.acty_key=acty.biz_acty_seq and acty.crnt_rec_flg=1 and acty.del_flg=0\r\n"
            + "where clnt_seq=app.clnt_seq and app.crnt_rec_flg=1 and l.loan_cycl_num=app.loan_cycl_num-1 and l.crnt_rec_flg=1 and l.loan_app_seq=app.loan_app_seq\r\n"
            + ")pre_acty,sysdate loan_app_sts_dt,prd.pdc_num, prd.prd_grp_seq\r\n" + "from mw_loan_app app \r\n"
            + "join mw_clnt clnt on app.clnt_seq=clnt.clnt_seq and clnt.crnt_rec_flg=1 \r\n"
            + "join mw_addr_rel ar on ar.enty_key=clnt.clnt_seq and ar.crnt_rec_flg=1 and ar.enty_typ='Client'\r\n"
            + "join mw_addr ad on ad.addr_seq=ar.addr_seq and ad.crnt_rec_flg=1\r\n"
            + "join mw_city_uc_rel cur on ad.city_seq=cur.city_uc_rel_seq and cur.crnt_rec_flg=1\r\n"
            + "join mw_city cit on cit.city_seq=cur.city_seq and cit.crnt_rec_flg=1\r\n"
            + "join mw_uc uc on uc.uc_seq=cur.uc_seq and uc.crnt_rec_flg=1\r\n"
            + "join mw_thsl thsl on thsl.thsl_seq=uc.thsl_seq and thsl.crnt_rec_flg=1\r\n"
            + "join mw_dist dist on dist.dist_seq=thsl.dist_seq and dist.crnt_rec_flg=1 \r\n"
            + "join mw_st st on st.st_seq=dist.st_seq and st.crnt_rec_flg=1 \r\n"
            + "join mw_cntry cntry on cntry.cntry_seq=st.cntry_seq and cntry.crnt_rec_flg=1 \r\n"
            + "join mw_port port on port.port_seq=app.port_seq and port.crnt_rec_flg=1\r\n"
            + "join mw_port_emp_rel poer on poer.port_seq=port.port_seq and poer.crnt_rec_flg=1\r\n"
            + "join mw_emp emp on emp.emp_seq=poer.emp_seq \r\n"
            + "join mw_brnch brnch on brnch.brnch_seq=port.brnch_seq and brnch.crnt_rec_flg=1 \r\n"
            + "join mw_area area on area.area_seq=brnch.area_seq and area.crnt_rec_flg=1 \r\n"
            + "join mw_reg reg on reg.reg_seq=area.reg_seq and reg.crnt_rec_flg=1 \r\n"
            + "join mw_prd prd on prd.prd_seq = app.prd_seq and prd.crnt_rec_flg=1 \r\n"
            + "join mw_prd_chrg pc on prd.prd_seq=pc.prd_seq and pc.crnt_rec_flg = 1 \r\n"
            + "join mw_typs pct on pct.TYP_SEQ=pc.CHRG_TYP_SEQ and pct.CRNT_REC_FLG=1 AND pct.TYP_ID='0017'\r\n"
            + "join mw_prd_chrg_slb  slb on slb.prd_chrg_seq=pc.prd_chrg_seq and slb.crnt_rec_flg=1 \r\n"
            + "left outer join mw_prd_chrg pc1 on prd.prd_seq=pc1.prd_seq and pc1.crnt_rec_flg = 1 and pc1.chrg_typ_seq=1\r\n"
            + "left outer join mw_pymt_sched_hdr psh on psh.loan_app_seq=app.loan_app_seq and psh.crnt_rec_flg=1\r\n"
            + "left outer join mw_biz_aprsl ba on ba.loan_app_seq=app.loan_app_seq and ba.crnt_rec_flg=1\r\n"
            + "left outer join mw_biz_acty acty on ba.acty_key=acty.biz_acty_seq and acty.crnt_rec_flg=1 and acty.del_flg=0\r\n"
            + "left outer join mw_biz_sect sect on sect.biz_sect_seq = ba.sect_key and sect.crnt_rec_flg=1\r\n"
            + "where app.crnt_rec_flg=1 and app.loan_app_seq =:loanAppSeq\r\n"
            + " order by app.crtd_dt desc";

    /*
     * public static String COMPLETE_APPLICATION_STS = "select ref_cd_seq" +
     * " from mw_ref_cd_val val" +
     * " join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.CRNT_REC_FLG=1 and grp.ref_cd_grp = '0106'"
     * + " where val.CRNT_REC_FLG=1 and val.ref_cd = '6'";
     */
    public static String TOTAL_RECEIVEABLE_AMOUNT = "select sum(ppal_amt_due)ppal_amt_due,sum(tot_chrg_due) tot_chrg_due,sum(amt) amt from\r\n"
            + "(select psd.ppal_amt_due,psd.tot_chrg_due,sum(psc.amt) amt\r\n" + "from mw_pymt_sched_hdr psh\r\n"
            + "join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\r\n"
            + "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq = psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1\r\n"
            + "where psh.loan_app_seq=:loanAppSeq and psh.crnt_rec_flg=1\r\n"
            + "group by psd.pymt_sched_dtl_seq,psd.ppal_amt_due,psd.tot_chrg_due\r\n" + ")";
    // ADDED BY YOUSAF DATEd: 14-OCT-2022:
    public static String TOTAL_RECEIVEABLE_AMOUNT_KTK = "select SUM(NVL(psd.ppal_amt_due,0)) ppal_amt_due,\n" +
            "            SUM(NVL(psd.tot_chrg_due,0)) tot_chrg_due, \n" +
            "            sum(NVL(psc.amt,0)) amt \n" +
            "            from mw_pymt_sched_hdr psh\n" +
            "            join mw_pymt_sched_dtl psd on psh.pymt_sched_hdr_seq=psd.pymt_sched_hdr_seq and psd.crnt_rec_flg=1\n" +
            "            left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq = psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1\n" +
            "            where psh.loan_app_seq in\n" +
            "            (\n" +
            "              select ap.loan_app_Seq\n" +
            "              from mw_loan_app ap\n" +
            "              where ap.clnt_Seq in\n" +
            "              (\n" +
            "                select la.CLNT_SEQ \n" +
            "                    from mw_loan_app la\n" +
            "                where la.LOAN_APP_SEQ = :loanAppSeq \n" +
            "                and la.CLNT_SEQ = ap.clnt_seq                \n" +
            "                and la.crnt_rec_flg = 1\n" +
            "              )\n" +
            "              and ap.loan_app_sts in (703,702) \n" +
            "            )\n" +
            "            and psh.crnt_rec_flg=1";
    public static String getDisbursmentApplications = "select l.loan_app_seq, l.loan_id, l.rqstd_loan_amt, l.aprvd_loan_amt, l.clnt_seq , p.prd_cmnt, l.rcmnd_loan_amt,\r\n"
            + "(select ref_cd_dscr from mw_ref_cd_val where ref_cd_seq=l.loan_app_sts and crnt_rec_flg=1) as status,\r\n"
            + "c.natr_of_dis_key, c.clnt_sts_key, c.res_typ_key, c.dis_flg, c.slf_pdc_flg, c.crnt_addr_perm_flg, c.ph_num, c.frst_nm, c.last_nm , l.crtd_dt, l.loan_app_sts_dt,po.port_cd,po.port_nm,emp.emp_nm bdo,p.prd_id,val.ref_cd, p.PRD_GRP_SEQ\r\n"
            + "from mw_loan_app l\r\n" + "join mw_clnt c on c.clnt_seq = l.clnt_seq and c.crnt_rec_flg=1\r\n"
            + "join mw_acl acl on acl.port_seq = l.port_seq and acl.user_id =:user \r\n"
            + "join mw_prd p on p.prd_seq =l.prd_seq  and p.crnt_rec_flg = 1\r\n"
            + "join mw_port po on po.port_seq=l.port_seq and po.crnt_rec_flg=1\r\n"
            + "join mw_port_emp_rel poer on poer.port_seq=po.port_seq and poer.crnt_rec_flg=1\r\n"
            + "join mw_emp emp on emp.emp_seq=poer.emp_seq\r\n"
            + "join mw_brnch brnch on brnch.brnch_seq=po.brnch_seq and brnch.brnch_seq = :brnch_seq and brnch.crnt_rec_flg=1\r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq=l.loan_app_sts and val.crnt_rec_flg=1 and (val.ref_cd = '0004' or val.ref_cd = '0009' or val.ref_cd = '1305' )\r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\r\n"
            + "where l.crnt_rec_flg=1 ";
    public static String getDisbursmentApplicationsActive = "select l.loan_app_seq, l.loan_id, l.rqstd_loan_amt, l.aprvd_loan_amt, l.clnt_seq , p.prd_cmnt, l.rcmnd_loan_amt,\r\n"
            + "(select ref_cd_dscr from mw_ref_cd_val where ref_cd_seq=l.loan_app_sts and crnt_rec_flg=1) as status,\r\n"
            + "c.natr_of_dis_key, c.clnt_sts_key, c.res_typ_key, c.dis_flg, c.slf_pdc_flg, c.crnt_addr_perm_flg, c.ph_num, c.frst_nm, c.last_nm , l.crtd_dt, l.loan_app_sts_dt,po.port_cd,po.port_nm,emp.emp_nm bdo,p.prd_id,val.ref_cd, p.PRD_GRP_SEQ\r\n"
            + "from mw_loan_app l\r\n" + "join mw_clnt c on c.clnt_seq = l.clnt_seq and c.crnt_rec_flg=1\r\n"
            + "join mw_prd p on p.prd_seq =l.prd_seq  and p.crnt_rec_flg = 1\r\n"
            + "join mw_port po on po.port_seq=l.port_seq and po.crnt_rec_flg=1\r\n"
            + "join mw_port_emp_rel poer on poer.port_seq=po.port_seq and poer.crnt_rec_flg=1\r\n"
            + "join mw_emp emp on emp.emp_seq=poer.emp_seq\r\n"
            + "join mw_brnch brnch on brnch.brnch_seq=po.brnch_seq and brnch.brnch_seq = :brnch_seq and brnch.crnt_rec_flg=1\r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq=l.loan_app_sts and val.crnt_rec_flg=1 and (val.ref_cd = '0005' or val.ref_cd = '1305')\r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\r\n"
            + "where l.crnt_rec_flg=1 ";
    public static String getDisbursmentApplicationsCount = "select  count(*)\n" +
            "        from mw_loan_app l join mw_clnt c on c.clnt_seq = l.clnt_seq and c.crnt_rec_flg=1\n" +
            "        join mw_acl acl on acl.port_seq = l.port_seq and acl.user_id =:user\n" +
            "        join mw_prd p on p.prd_seq =l.prd_seq  and p.crnt_rec_flg = 1\n" +
            "        join mw_port po on po.port_seq=l.port_seq and po.crnt_rec_flg=1\n" +
            "        join mw_port_emp_rel poer on poer.port_seq=po.port_seq and poer.crnt_rec_flg=1\n" +
            "        join mw_emp emp on emp.emp_seq=poer.emp_seq\n" +
            "        join mw_brnch brnch on brnch.brnch_seq= po.brnch_seq and brnch.brnch_seq = :brnch_seq and brnch.crnt_rec_flg=1\n" +
            "        join mw_ref_cd_val val on val.ref_cd_seq=l.loan_app_sts and val.crnt_rec_flg=1 and (val.ref_cd = '0004' or val.ref_cd = '0009' or val.ref_cd = '1305' )\n" +
            "        join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\n" +
            "        where l.crnt_rec_flg=1";
    public static String getDisbursmentApplicationsActiveCount = "select      count(*)\n" +
            "            from mw_loan_app l join mw_clnt c on c.clnt_seq = l.clnt_seq and c.crnt_rec_flg=1\n" +
            "            join mw_prd p on p.prd_seq =l.prd_seq  and p.crnt_rec_flg = 1\n" +
            "            join mw_port po on po.port_seq=l.port_seq and po.crnt_rec_flg=1\n" +
            "            join mw_port_emp_rel poer on poer.port_seq=po.port_seq and poer.crnt_rec_flg=1\n" +
            "            join mw_emp emp on emp.emp_seq=poer.emp_seq\n" +
            "            join mw_brnch brnch on brnch.brnch_seq=po.brnch_seq and brnch.brnch_seq = :brnch_seq and brnch.crnt_rec_flg=1\n" +
            "            join mw_ref_cd_val val on val.ref_cd_seq=l.loan_app_sts and val.crnt_rec_flg=1 and (val.ref_cd = '0005' or val.ref_cd = '1305')\n" +
            "            join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\n" +
            "            where l.crnt_rec_flg=1 ";
    public static String UNDERTAKING_INFO_QUERY = "select app.loan_id,clnt.frst_nm, clnt.last_nm,clnt.fthr_frst_nm||clnt.spz_frst_nm,clnt.fthr_last_nm||clnt.spz_last_nm, clnt.clnt_id, clnt.cnic_num,  \r\n"
            + "port.port_nm, brnch.brnch_nm, area.area_nm, reg.reg_nm,p.prd_id,sect.BIZ_SECT_NM,ba.BIZ_DTL_STR  \r\n"
            + "from mw_loan_app app \r\n" + "join mw_prd p on p.prd_seq=app.prd_seq and p.crnt_rec_flg=1   \r\n"
            + "join mw_clnt clnt on app.clnt_seq=clnt.clnt_seq and clnt.crnt_rec_flg=1 \r\n"
            + "left outer join MW_BIZ_APRSL ba on ba.LOAN_APP_SEQ=app.LOAN_APP_SEQ and ba.CRNT_REC_FLG = 1\r\n"
            + "left outer join MW_BIZ_ACTY acty on acty.BIZ_ACTY_SEQ=ba.ACTY_KEY and acty.CRNT_REC_FLG = 1 \r\n"
            + "left outer join mw_biz_sect sect on acty.BIZ_SECT_SEQ=sect.BIZ_SECT_SEQ and sect.CRNT_REC_FLG = 1  \r\n"
            + "join mw_port port on port.port_seq=app.port_seq and port.crnt_rec_flg=1  \r\n"
            + "join mw_brnch brnch on brnch.brnch_seq=port.brnch_seq and brnch.crnt_rec_flg=1  \r\n"
            + "join mw_area area on area.area_seq=brnch.area_seq and area.crnt_rec_flg=1  \r\n"
            + "join mw_reg reg on reg.reg_seq=area.reg_seq and reg.crnt_rec_flg=1  \r\n"
            + "where app.crnt_rec_flg=1 AND app.LOAN_APP_SEQ  =:loanAppSeq";
    public static String HLTH_INSR_CARD_QUERY = "select card.card_num,card.card_expiry_dt,clnt.clnt_seq,clnt.frst_nm, clnt.last_nm,clnt.dob,clnt.fthr_frst_nm,clnt.fthr_last_nm, clnt.clnt_id, clnt.cnic_num,port.port_nm, brnch.brnch_nm, plan.max_plcy_amt"
            + " from mw_loan_app app " + " join mw_clnt clnt on app.clnt_seq=clnt.clnt_seq and clnt.crnt_rec_flg=1   "
            + " join mw_port port on port.port_seq=app.port_seq and port.crnt_rec_flg=1   "
            + " join mw_brnch brnch on brnch.brnch_seq=port.brnch_seq and brnch.crnt_rec_flg=1   "
            + " left outer join mw_clnt_hlth_insr_card card on card.loan_app_seq=app.loan_app_seq and card.crnt_rec_flg=1   "
            + " join mw_clnt_hlth_insr insr on insr.loan_app_seq=app.loan_app_seq and insr.crnt_rec_flg=1   "
            + " join mw_hlth_insr_plan plan on plan.hlth_insr_plan_seq=insr.hlth_insr_plan_seq and plan.crnt_rec_flg=1   "
            + " where app.crnt_rec_flg=1 AND app.LOAN_APP_SEQ =:loanAppSeq";
    public static String APPLICATION_STS = "select ref_cd_seq" + " from mw_ref_cd_val val"
            + " join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.CRNT_REC_FLG=1 and grp.ref_cd_grp = '0106'"
            + " where val.CRNT_REC_FLG=1 and val.ref_cd =:refCd";
    public static String ALLRECOVERYLIST = "select psd.pymt_sched_dtl_seq,c.clnt_id,c.frst_nm,c.last_nm,psd.inst_num,\r\n"
            + "psd.ppal_amt_due,psd.tot_chrg_due,sum(psc.amt) amt,psd.due_dt,nd.next_due,sts.ref_cd_dscr sts,e.EMP_NM,\r\n"
            + "pymt.rcvry_trx_seq, pymt.pymt_dt,pymt.instr_num,\r\n"
            + "pymt.typ_str,pymt.pymt_amt,pymt.post_flg,pymt.trx_pymt,pymt.rcvry_typ_seq,prd.PRD_CMNT\r\n"
            + "from mw_pymt_sched_dtl psd \r\n" + "left outer join \r\n" + "(select pymt_sched_dtl_seq,\r\n"
            + "listagg( rcvry_trx_seq,',') within group (order by rcvry_trx_seq) rcvry_trx_seq,\r\n"
            + "listagg( pymt_dt,',') within group (order by rcvry_trx_seq) pymt_dt,\r\n"
            + "listagg( typ_str,',') within group (order by rcvry_trx_seq) typ_str,\r\n"
            + "listagg( instr_num,',') within group (order by rcvry_trx_seq) instr_num,\r\n"
            + "listagg( pymt_amt,',') within group (order by rcvry_trx_seq) pymt_amt,\r\n"
            + "listagg( post_flg,',') within group (order by rcvry_trx_seq) post_flg,\r\n"
            + "listagg( trx_pymt,',') within group (order by rcvry_trx_seq) trx_pymt,\r\n"
            + "listagg( rcvry_typ_seq,',') within group (order by rcvry_trx_seq) rcvry_typ_seq\r\n"
            + "from( select dtl.pymt_sched_dtl_seq,trx.rcvry_trx_seq,nvl(trx.post_flg,0)post_flg,trx.pymt_dt,typ.typ_str,trx.instr_num,sum(dtl.pymt_amt) pymt_amt,trx.pymt_amt trx_pymt,trx.rcvry_typ_seq\r\n"
            + "from mw_rcvry_dtl dtl\r\n" + "join mw_rcvry_trx trx on trx.rcvry_trx_seq = dtl.rcvry_trx_seq\r\n"
            + "join mw_typs typ on typ.typ_seq = trx.rcvry_typ_seq where dtl.crnt_rec_flg = 1\r\n"
            + "and trx.crnt_rec_flg = 1 and typ.crnt_rec_flg = 1\r\n"
            + "group by dtl.pymt_sched_dtl_seq,trx.rcvry_trx_seq,trx.post_flg,trx.pymt_dt,typ.typ_str,trx.instr_num,trx.pymt_amt,trx.rcvry_typ_seq\r\n"
            + ") group by pymt_sched_dtl_seq,pymt_dt ) pymt on pymt.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq\r\n"
            + "join mw_pymt_sched_hdr psh on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psh.crnt_rec_flg=1\r\n"
            + "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq = psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1\r\n"
            + "join (select hdr.loan_app_seq,min(due_dt) next_due from mw_pymt_sched_hdr hdr join mw_pymt_sched_dtl dtl on hdr.pymt_sched_hdr_seq=dtl.pymt_sched_hdr_seq and dtl.crnt_rec_flg=1 where hdr.crnt_rec_flg=1 and dtl.pymt_sts_key=945 group by hdr.loan_app_seq) nd\r\n"
            + "      on nd.loan_app_seq=psh.loan_app_seq\r\n"
            + "join mw_loan_app la on la.loan_app_seq=psh.loan_app_seq and la.crnt_rec_flg=1\r\n"
            + "JOIN mw_acl acl ON acl.port_seq = la.port_seq AND acl.user_id =:userId\r\n"
            + "join mw_prd prd on prd.PRD_SEQ=la.PRD_SEQ and prd.CRNT_REC_FLG = 1\r\n"
            + "join mw_clnt c on c.clnt_seq=la.clnt_seq and c.crnt_rec_flg=1\r\n"
            + "join mw_port p on la.port_seq=p.port_seq and p.crnt_rec_flg=1\r\n"
            + "join mw_port_emp_rel per on per.PORT_SEQ=p.PORT_SEQ and per.CRNT_REC_FLG = 1\r\n"
            + "join mw_emp e on e.EMP_SEQ=per.EMP_SEQ\r\n"
            + "left outer join mw_ref_cd_val sts on psd.pymt_sts_key=sts.ref_cd_seq and sts.crnt_rec_flg=1\r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq=la.loan_app_sts and val.crnt_rec_flg=1 and (val.ref_cd = '0005' or val.ref_cd ='1245')\r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\r\n"
            + "where psd.crnt_rec_flg=1 and la.LOAN_CYCL_NUM=(select max(loan_cycl_num) from mw_loan_app ap where ap.clnt_seq=c.clnt_seq)\r\n"
            + "group by psd.pymt_sched_dtl_seq,pymt_dt\r\n" + ",c.clnt_id,c.frst_nm,c.last_nm,la.prnt_loan_app_seq,la.loan_id,\r\n"
            + "psd.inst_num,psd.ppal_amt_due,psd.tot_chrg_due,psd.due_dt,nd.next_due,sts.ref_cd_dscr,e.EMP_NM,\r\n"
            + "pymt.rcvry_trx_seq,pymt.pymt_dt,pymt.instr_num,pymt.typ_str,pymt.pymt_amt,pymt.post_flg,pymt.trx_pymt,pymt.rcvry_typ_seq,\r\n"
            + "prd.PRD_CMNT\r\n" + "order by nd.next_due ,la.prnt_loan_app_seq desc, psd.inst_num asc,psd.due_dt";
    public static String ALLONERECOVERYBYLOANAPP = "select psd.pymt_sched_dtl_seq,psd.inst_num, \n" +
            "psd.ppal_amt_due,psd.tot_chrg_due,sum(psc.amt) amt,psd.due_dt,sts.ref_cd_dscr sts, \n" +
            "pymt.rcvry_trx_seq, pymt.pymt_dt,pymt.instr_num, \n" +
            "pymt.typ_str,pymt.pymt_amt,pymt.post_flg,pymt.trx_pymt,pymt.rcvry_typ_seq,prd.PRD_CMNT,\n" +
            "case when trf_CLNT_SEQ is not null then 'T' else null end trf_flg \n" +
            "from mw_pymt_sched_dtl psd left outer join (select pymt_sched_dtl_seq, CLNT_SEQ trf_CLNT_SEQ,\n" +
            "listagg( rcvry_trx_seq,',') within group (order by rcvry_trx_seq) rcvry_trx_seq, \n" +
            "listagg( pymt_dt,',') within group (order by rcvry_trx_seq) pymt_dt, \n" +
            "listagg( typ_str,',') within group (order by rcvry_trx_seq) typ_str, \n" +
            "listagg( nvl(instr_num,rcvry_trx_seq),',') within group (order by rcvry_trx_seq) instr_num, \n" +
            "listagg( pymt_amt,',') within group (order by rcvry_trx_seq) pymt_amt, \n" +
            "listagg( post_flg,',') within group (order by rcvry_trx_seq) post_flg, \n" +
            "listagg( trx_pymt,',') within group (order by rcvry_trx_seq) trx_pymt, \n" +
            "listagg( rcvry_typ_seq,',') within group (order by rcvry_trx_seq) rcvry_typ_seq \n" +
            "from( select dtl.pymt_sched_dtl_seq,trx.rcvry_trx_seq,nvl(trx.post_flg,0)post_flg,trx.pymt_dt,typ.typ_str,trx.instr_num,sum(dtl.pymt_amt) pymt_amt,trx.pymt_amt trx_pymt,trx.rcvry_typ_seq,\n" +
            "trf.CLNT_SEQ \n" +
            "from mw_rcvry_dtl dtl  join mw_rcvry_trx trx on trx.rcvry_trx_seq = dtl.rcvry_trx_seq \n" +
            "left outer join RPTB_PORT_TRF_DETAIL trf on trf.CLNT_SEQ = to_number(trx.pymt_ref) and trx.EFF_START_DT <= trf.TRF_DT and trf.REMARKS = 'LOAN'\n" +
            "join mw_typs typ on typ.typ_seq = trx.rcvry_typ_seq where dtl.crnt_rec_flg = 1 \n" +
            "and trx.crnt_rec_flg = 1 and typ.crnt_rec_flg = 1 and trx.pymt_ref = :clntSeq \n" +
            "group by dtl.pymt_sched_dtl_seq,trx.rcvry_trx_seq,trx.post_flg,trx.pymt_dt,typ.typ_str,trx.instr_num,trx.pymt_amt,trx.rcvry_typ_seq,\n" +
            "trf.CLNT_SEQ\n" +
            ")group by pymt_sched_dtl_seq,CLNT_SEQ) pymt on pymt.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq \n" +
            "join mw_pymt_sched_hdr psh on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psh.crnt_rec_flg=1 \n" +
            "left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq = psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1  \n" +
            "join mw_loan_app la on la.loan_app_seq=psh.loan_app_seq and la.crnt_rec_flg=1 \n" +
            "join mw_prd prd on prd.PRD_SEQ=la.PRD_SEQ and prd.CRNT_REC_FLG = 1 \n" +
            "join mw_clnt c on c.clnt_seq=la.clnt_seq and c.crnt_rec_flg=1 \n" +
            "left outer join mw_ref_cd_val sts on psd.pymt_sts_key=sts.ref_cd_seq and sts.crnt_rec_flg=1 \n" +
            "join mw_ref_cd_val val on val.ref_cd_seq=la.loan_app_sts and val.crnt_rec_flg=1 and (val.ref_cd = '0005' or val.ref_cd ='1245' or val.ref_cd ='0006')  \n" +
            "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106' \n" +
            "where psd.crnt_rec_flg=1 and c.CLNT_SEQ = :clntSeq and la.LOAN_CYCL_NUM=(select max(loan_cycl_num) from mw_loan_app ap where ap.clnt_seq=c.clnt_seq and ap.crnt_rec_flg=1 and ap.loan_app_sts in (703, 704)) \n" +
            "group by psd.pymt_sched_dtl_seq,c.clnt_id,c.frst_nm,c.last_nm,la.prnt_loan_app_seq,la.loan_id, \n" +
            "psd.inst_num,psd.ppal_amt_due,psd.tot_chrg_due,psd.due_dt,sts.ref_cd_dscr, \n" +
            "pymt.rcvry_trx_seq,pymt.pymt_dt,pymt.instr_num,pymt.typ_str,pymt.pymt_amt,pymt.post_flg,pymt.trx_pymt,pymt.rcvry_typ_seq,prd.PRD_CMNT,\n" +
            "trf_CLNT_SEQ \n" +
            "order by psd.due_dt,la.prnt_loan_app_seq desc";
    //Modified by Areeba - for adc recoveries - 16-9-2022
    public static String ALLONERECOVERYBYPRNTLOANAPP = "select psd.pymt_sched_dtl_seq,psd.inst_num,\n" +
            "        psd.ppal_amt_due,psd.tot_chrg_due,sum(psc.amt) amt,psd.due_dt,sts.ref_cd_dscr sts,\n" +
            "        pymt.rcvry_trx_seq, pymt.pymt_dt,pymt.instr_num,\n" +
            "        pymt.typ_str,pymt.brnch_seq,pymt.pymt_amt,pymt.post_flg,pymt.trx_pymt,pymt.rcvry_typ_seq,prd.PRD_CMNT,\n" +
            "        case when trf_CLNT_SEQ is not null then 'T' else null end trf_flg\n" +
            "        from mw_pymt_sched_dtl psd left outer join (\n" +
            "        select pymt_sched_dtl_seq, CLNT_SEQ trf_CLNT_SEQ,\n" +
            "        listagg( rcvry_trx_seq,',') within group (order by rcvry_trx_seq) rcvry_trx_seq,\n" +
            "        listagg( pymt_dt,',') within group (order by rcvry_trx_seq) pymt_dt,\n" +
            "        listagg( typ_str,',') within group (order by rcvry_trx_seq) typ_str,\n" +
            "        --listagg( typ_ctgry_key,',') within group (order by rcvry_trx_seq) typ_ctgry_key,\n" +
            "        listagg( brnch_seq,',') within group (order by rcvry_trx_seq) brnch_seq,\n" +
            "        listagg( nvl(instr_num,rcvry_trx_seq),',') within group (order by rcvry_trx_seq) instr_num,\n" +
            "        listagg( pymt_amt,',') within group (order by rcvry_trx_seq) pymt_amt,\n" +
            "        listagg( post_flg,',') within group (order by rcvry_trx_seq) post_flg,\n" +
            "        listagg( trx_pymt,',') within group (order by rcvry_trx_seq) trx_pymt,\n" +
            "        listagg( rcvry_typ_seq,',') within group (order by rcvry_trx_seq) rcvry_typ_seq\n" +
            "        from( select dtl.pymt_sched_dtl_seq,trx.rcvry_trx_seq,nvl(trx.post_flg,0)post_flg,trx.pymt_dt,typ.typ_str,typ.brnch_seq,trx.instr_num,sum(dtl.pymt_amt) pymt_amt,trx.pymt_amt trx_pymt,\n" +
            "        trx.rcvry_typ_seq,\n" +
            "        trf.CLNT_SEQ\n" +
            "        from mw_rcvry_dtl dtl  join mw_rcvry_trx trx on trx.rcvry_trx_seq = dtl.rcvry_trx_seq\n" +
            "        left outer join RPTB_PORT_TRF_DETAIL trf on trf.CLNT_SEQ = to_number(trx.pymt_ref) and trx.EFF_START_DT <= trf.TRF_DT and trf.REMARKS = 'LOAN'\n" +
            "        join mw_typs typ on typ.typ_seq = trx.rcvry_typ_seq where dtl.crnt_rec_flg = 1\n" +
            "        and trx.crnt_rec_flg = 1 and typ.crnt_rec_flg = 1 and to_number(trx.pymt_ref) = :clntSeq\n" +
            "        group by dtl.pymt_sched_dtl_seq,trx.rcvry_trx_seq,trx.post_flg,trx.pymt_dt,typ.typ_str,typ.brnch_seq,trx.instr_num,trx.pymt_amt,trx.rcvry_typ_seq,\n" +
            "        trf.CLNT_SEQ\n" +
            "        )group by pymt_sched_dtl_seq,CLNT_SEQ\n" +
            "        ) pymt on pymt.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq\n" +
            "        join mw_pymt_sched_hdr psh on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psh.crnt_rec_flg=1\n" +
            "        left outer join mw_pymt_sched_chrg psc on psd.pymt_sched_dtl_seq = psc.pymt_sched_dtl_seq and psc.crnt_rec_flg=1\n" +
            "        join mw_loan_app la on la.loan_app_seq=psh.loan_app_seq and la.crnt_rec_flg=1\n" +
            "        join mw_prd prd on prd.PRD_SEQ=la.PRD_SEQ and prd.CRNT_REC_FLG = 1\n" +
            "        join mw_clnt c on c.clnt_seq=la.clnt_seq and c.crnt_rec_flg=1\n" +
            "        left outer join mw_ref_cd_val sts on psd.pymt_sts_key=sts.ref_cd_seq and sts.crnt_rec_flg=1\n" +
            "        join mw_ref_cd_val val on val.ref_cd_seq=la.loan_app_sts and val.crnt_rec_flg=1 and (val.ref_cd = '0005' or val.ref_cd ='1245' or val.ref_cd ='0006')\n" +
            "        join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\n" +
            "        where psd.crnt_rec_flg=1 and c.CLNT_SEQ = :clntSeq and (la.LOAN_CYCL_NUM=(select max(loan_cycl_num) from mw_loan_app ap \n" +
            "                                where ap.clnt_seq=c.clnt_seq and ap.crnt_rec_flg=1 and ap.loan_app_sts in (703, 704)\n" +
            "                                and ap.prd_seq = (select max(prd.prd_seq) from mw_prd prd where upper(prd.PRD_CMNT) = upper(:prd) AND PRD.CRNT_REC_FLG = 1 \n" +
            "                                AND prd.PRD_STS_KEY = 200))\n" +
            "                                or (la.LOAN_CYCL_NUM=(select max(loan_cycl_num) from mw_loan_app ap \n" +
            "                                where ap.clnt_seq=c.clnt_seq and ap.crnt_rec_flg=1 and ap.loan_app_sts in (703, 704)\n" +
            "                                and ap.PRNT_LOAN_APP_SEQ = :prntLoanAppSeq)))\n" +
            "        group by psd.pymt_sched_dtl_seq,c.clnt_id,c.frst_nm,c.last_nm,la.prnt_loan_app_seq,la.loan_id,\n" +
            "        psd.inst_num,psd.ppal_amt_due,psd.tot_chrg_due,psd.due_dt,sts.ref_cd_dscr,\n" +
            "        pymt.rcvry_trx_seq,pymt.pymt_dt,pymt.instr_num,pymt.typ_str,pymt.brnch_seq,pymt.pymt_amt,pymt.post_flg,pymt.trx_pymt,pymt.rcvry_typ_seq,prd.PRD_CMNT,\n" +
            "        trf_CLNT_SEQ\n" +
            "        order by psd.due_dt,la.prnt_loan_app_seq desc";
    public static String REPAYMENT = "select ppl.due_dt,                       max(ppal_amt_due),   \r\n"
            + "       max(tot_chrg_due),               typ_str,                 max(amt),   \r\n"
            + "       max(nvl(ost_ppl,0)+nvl(ost_chrgs,0)) outstanding       from          ( select la.prnt_loan_app_seq loan_app_seq,   \r\n"
            + "               psd.inst_num,                                trunc(psd.due_dt) due_dt,   \r\n"
            + "               sum(psd.ppal_amt_due) over (partition by psd.due_dt) ppal_amt_due,   \r\n"
            + "               sum(psd.tot_chrg_due) over (partition by psd.due_dt) tot_chrg_due,   \r\n"
            + "               sum(psd.ppal_amt_due+psd.tot_chrg_due) over (order by la.loan_app_seq desc,psd.inst_num desc ,trunc(psd.due_dt) desc) ost_ppl   \r\n"
            + "        from mw_loan_app la   \r\n"
            + "        join mw_pymt_sched_hdr psh on la.loan_app_seq=psh.loan_app_seq and psh.crnt_rec_flg=1   \r\n"
            + "        join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg = 1   \r\n"
            + "        where la.PRNT_LOAN_APP_SEQ=:loanAppSeq and la.clnt_seq = :clntseq and la.crnt_rec_flg=1         \r\n"
            + "        order by inst_num      ) ppl   left outer join (            \r\n"
            + "           select  app.loan_app_seq,                       psd.inst_num,   \r\n"
            + "           trunc(psd.due_dt) due_dt,                       nvl(t.TYP_STR,PLN_DSCR) typ_str,   \r\n"
            + "           psc.CHRG_TYPS_SEQ,              sum(psc.AMT) over (partition by app.loan_app_seq,psd.inst_num,  \r\n"
            + "           trunc(psd.due_dt),             psc.CHRG_TYPS_SEQ) amt,   \r\n"
            + "           sum(psc.AMT) over (order by app.loan_app_seq ,psd.due_dt desc) ost_chrgs   \r\n"
            + "        from mw_loan_app app   \r\n"
            + "        join mw_pymt_sched_hdr psh on psh.loan_app_seq=app.loan_app_seq and psh.crnt_rec_flg=1   \r\n"
            + "        join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg = 1   \r\n"
            + "        left outer join mw_pymt_sched_chrg psc on psc.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and psc.crnt_rec_flg =1   \r\n"
            + "        left outer join mw_typs t on t.TYP_SEQ=psc.CHRG_TYPS_SEQ and t.CRNT_REC_FLG = 1   \r\n"
            + "        left outer join (select chi.loan_app_seq,hip.PLN_DSCR                       from  MW_CLNT_HLTH_INSR chi   \r\n"
            + "                    join MW_HLTH_INSR_PLAN hip on hip.HLTH_INSR_PLAN_SEQ = chi.HLTH_INSR_PLAN_SEQ and hip.CRNT_REC_FLG=1   \r\n"
            + "                    where chi.CRNT_REC_FLG=1) hpln on hpln.loan_app_seq=app.prnt_loan_app_seq   \r\n"
            + "        where app.PRNT_LOAN_APP_SEQ=:loanAppSeq and app.clnt_seq = :clntseq and app.crnt_rec_flg=1 and amt is not null  \r\n"
            + "        order by inst_num) chrg on chrg.loan_app_seq=ppl.loan_app_seq and chrg.inst_num=ppl.inst_num   \r\n"
            + "group by ppl.due_dt,typ_str   \r\n" + " order by 1";
    // ADDED BY YOUSAF DATEd: 14-OCT-2022:
    public static String REPAYMENT_KTK = "SELECT due_dt,\n" +
            "         ppal_amt_due,\n" +
            "         tot_chrg_due,\n" +
            "         typ_str,\n" +
            "         amt,\n" +
            "         CASE\n" +
            "             WHEN PRD_SEQ = 4\n" +
            "             THEN\n" +
            "                 SUM (outstanding_kkk) OVER (ORDER BY due_dt DESC)\n" +
            "             ELSE\n" +
            "                 0\n" +
            "         END\n" +
            "             ost_ppl_kkk,\n" +
            "         CASE\n" +
            "             WHEN PRD_SEQ = 51\n" +
            "             THEN\n" +
            "                 SUM (outstanding_ktk) OVER (ORDER BY due_dt DESC)\n" +
            "             ELSE\n" +
            "                 0\n" +
            "         END\n" +
            "             ost_ppl_ktk,\n" +
            "         PRD_CMNT\n" +
            "    FROM (  SELECT TRUNC (psd.due_dt)\n" +
            "                       due_dt,\n" +
            "                   SUM (psd.ppal_amt_due)\n" +
            "                       ppal_amt_due,\n" +
            "                   SUM (psd.tot_chrg_due)\n" +
            "                       tot_chrg_due,\n" +
            "                   MAX (typ_str)\n" +
            "                       typ_str,\n" +
            "                   SUM (amt)\n" +
            "                       amt,\n" +
            "                   SUM (\n" +
            "                         NVL (psd.ppal_amt_due, 0)\n" +
            "                       + NVL (psd.tot_chrg_due, 0)\n" +
            "                       + NVL (amt, 0))\n" +
            "                       outstanding_kkk,\n" +
            "                   NULL\n" +
            "                       outstanding_ktk,\n" +
            "                   CASE\n" +
            "                       WHEN la.PRD_SEQ IN (29, 4) THEN 'KKK <KSK>'\n" +
            "                       ELSE prd.PRD_CMNT\n" +
            "                   END\n" +
            "                       PRD_CMNT,\n" +
            "                   CASE WHEN la.PRD_SEQ IN (29, 4) THEN 4 ELSE 51 END\n" +
            "                       PRD_SEQ\n" +
            "              FROM mw_loan_app la\n" +
            "                   JOIN mw_pymt_sched_hdr psh\n" +
            "                       ON     la.loan_app_seq = psh.loan_app_seq\n" +
            "                          AND psh.crnt_rec_flg = 1\n" +
            "                   JOIN mw_pymt_sched_dtl psd\n" +
            "                       ON     psd.pymt_sched_hdr_seq = psh.pymt_sched_hdr_seq\n" +
            "                          AND psd.crnt_rec_flg = 1\n" +
            "                   JOIN mw_prd prd\n" +
            "                       ON prd.PRD_SEQ = la.PRD_SEQ AND prd.CRNT_REC_FLG = 1\n" +
            "                   LEFT OUTER JOIN\n" +
            "                   (  SELECT app.prnt_loan_app_seq         loan_app_seq,\n" +
            "                             TRUNC (psd.due_dt)            due_dt,\n" +
            "                             NVL (t.TYP_STR, PLN_DSCR)     typ_str,\n" +
            "                             psc.CHRG_TYPS_SEQ,\n" +
            "                             SUM (psc.AMT)                 amt,\n" +
            "                             SUM (psc.AMT)                 ost_chrgs\n" +
            "                        FROM mw_loan_app app\n" +
            "                             JOIN mw_pymt_sched_hdr psh\n" +
            "                                 ON     psh.loan_app_seq = app.loan_app_seq\n" +
            "                                    AND psh.crnt_rec_flg = 1\n" +
            "                             JOIN mw_pymt_sched_dtl psd\n" +
            "                                 ON     psd.pymt_sched_hdr_seq =\n" +
            "                                        psh.pymt_sched_hdr_seq\n" +
            "                                    AND psd.crnt_rec_flg = 1\n" +
            "                             LEFT OUTER JOIN mw_pymt_sched_chrg psc\n" +
            "                                 ON     psc.pymt_sched_dtl_seq =\n" +
            "                                        psd.pymt_sched_dtl_seq\n" +
            "                                    AND psc.crnt_rec_flg = 1\n" +
            "                             LEFT OUTER JOIN mw_typs t\n" +
            "                                 ON     t.TYP_SEQ = psc.CHRG_TYPS_SEQ\n" +
            "                                    AND t.CRNT_REC_FLG = 1\n" +
            "                             LEFT OUTER JOIN\n" +
            "                             (SELECT chi.loan_app_seq,\n" +
            "                                     CASE\n" +
            "                                         WHEN UPPER (hip.PLN_DSCR) LIKE\n" +
            "                                                  UPPER ('%KSZB%')\n" +
            "                                         THEN\n" +
            "                                             'KSZB FEE'\n" +
            "                                         ELSE\n" +
            "                                             UPPER (hip.PLN_DSCR)\n" +
            "                                     END\n" +
            "                                         PLN_DSCR\n" +
            "                                FROM MW_CLNT_HLTH_INSR chi\n" +
            "                                     JOIN MW_HLTH_INSR_PLAN hip\n" +
            "                                         ON     hip.HLTH_INSR_PLAN_SEQ =\n" +
            "                                                chi.HLTH_INSR_PLAN_SEQ\n" +
            "                                            AND hip.CRNT_REC_FLG = 1\n" +
            "                               WHERE chi.CRNT_REC_FLG = 1) hpln\n" +
            "                                 ON hpln.loan_app_seq = app.prnt_loan_app_seq\n" +
            "                       WHERE     app.loan_app_sts IN (702, 703)\n" +
            "                             AND app.clnt_seq = :clntseq\n" +
            "                             AND app.crnt_rec_flg = 1\n" +
            "                             AND amt IS NOT NULL\n" +
            "                    GROUP BY app.prnt_loan_app_seq,\n" +
            "                             TRUNC (psd.due_dt),\n" +
            "                             NVL (t.TYP_STR, PLN_DSCR),\n" +
            "                             psc.CHRG_TYPS_SEQ\n" +
            "                    ORDER BY TRUNC (psd.due_dt), app.prnt_loan_app_seq) chrg\n" +
            "                       ON     chrg.loan_app_seq = la.loan_app_seq\n" +
            "                          AND chrg.due_dt = psd.due_dt\n" +
            "             WHERE     la.clnt_seq = :clntseq\n" +
            "                   AND la.crnt_rec_flg = 1\n" +
            "                   AND la.loan_app_sts IN (702, 703)\n" +
            "                   AND la.PRD_SEQ != 51\n" +
            "          GROUP BY TRUNC (psd.due_dt),\n" +
            "                   CASE\n" +
            "                       WHEN la.PRD_SEQ IN (29, 4) THEN 'KKK <KSK>'\n" +
            "                       ELSE prd.PRD_CMNT\n" +
            "                   END,\n" +
            "                   CASE WHEN la.PRD_SEQ IN (29, 4) THEN 4 ELSE 51 END\n" +
            "          UNION\n" +
            "            SELECT TRUNC (psd.due_dt)\n" +
            "                       due_dt,\n" +
            "                   SUM (psd.ppal_amt_due)\n" +
            "                       ppal_amt_due,\n" +
            "                   SUM (psd.tot_chrg_due)\n" +
            "                       tot_chrg_due,\n" +
            "                   MAX (typ_str)\n" +
            "                       typ_str,\n" +
            "                   SUM (amt)\n" +
            "                       amt,\n" +
            "                   NULL,\n" +
            "                   SUM (\n" +
            "                         NVL (psd.ppal_amt_due, 0)\n" +
            "                       + NVL (psd.tot_chrg_due, 0)\n" +
            "                       + NVL (amt, 0))\n" +
            "                       outstanding_ktk,\n" +
            "                   CASE\n" +
            "                       WHEN la.PRD_SEQ IN (29, 4) THEN 'KKK <KSK>'\n" +
            "                       ELSE prd.PRD_CMNT\n" +
            "                   END\n" +
            "                       PRD_CMNT,\n" +
            "                   CASE WHEN la.PRD_SEQ IN (29, 4) THEN 4 ELSE 51 END\n" +
            "                       PRD_SEQ\n" +
            "              FROM mw_loan_app la\n" +
            "                   JOIN mw_pymt_sched_hdr psh\n" +
            "                       ON     la.loan_app_seq = psh.loan_app_seq\n" +
            "                          AND psh.crnt_rec_flg = 1\n" +
            "                   JOIN mw_pymt_sched_dtl psd\n" +
            "                       ON     psd.pymt_sched_hdr_seq = psh.pymt_sched_hdr_seq\n" +
            "                          AND psd.crnt_rec_flg = 1\n" +
            "                   JOIN mw_prd prd\n" +
            "                       ON prd.PRD_SEQ = la.PRD_SEQ AND prd.CRNT_REC_FLG = 1\n" +
            "                   LEFT OUTER JOIN\n" +
            "                   (  SELECT app.prnt_loan_app_seq         loan_app_seq,\n" +
            "                             TRUNC (psd.due_dt)            due_dt,\n" +
            "                             NVL (t.TYP_STR, PLN_DSCR)     typ_str,\n" +
            "                             psc.CHRG_TYPS_SEQ,\n" +
            "                             SUM (psc.AMT)                 amt,\n" +
            "                             SUM (psc.AMT)                 ost_chrgs\n" +
            "                        FROM mw_loan_app app\n" +
            "                             JOIN mw_pymt_sched_hdr psh\n" +
            "                                 ON     psh.loan_app_seq = app.loan_app_seq\n" +
            "                                    AND psh.crnt_rec_flg = 1\n" +
            "                             JOIN mw_pymt_sched_dtl psd\n" +
            "                                 ON     psd.pymt_sched_hdr_seq =\n" +
            "                                        psh.pymt_sched_hdr_seq\n" +
            "                                    AND psd.crnt_rec_flg = 1\n" +
            "                             LEFT OUTER JOIN mw_pymt_sched_chrg psc\n" +
            "                                 ON     psc.pymt_sched_dtl_seq =\n" +
            "                                        psd.pymt_sched_dtl_seq\n" +
            "                                    AND psc.crnt_rec_flg = 1\n" +
            "                             LEFT OUTER JOIN mw_typs t\n" +
            "                                 ON     t.TYP_SEQ = psc.CHRG_TYPS_SEQ\n" +
            "                                    AND t.CRNT_REC_FLG = 1\n" +
            "                             LEFT OUTER JOIN\n" +
            "                             (SELECT chi.loan_app_seq,\n" +
            "                                     CASE\n" +
            "                                         WHEN UPPER (hip.PLN_DSCR) LIKE\n" +
            "                                                  UPPER ('%KSZB%')\n" +
            "                                         THEN\n" +
            "                                             'KSZB FEE'\n" +
            "                                         ELSE\n" +
            "                                             UPPER (hip.PLN_DSCR)\n" +
            "                                     END\n" +
            "                                         PLN_DSCR\n" +
            "                                FROM MW_CLNT_HLTH_INSR chi\n" +
            "                                     JOIN MW_HLTH_INSR_PLAN hip\n" +
            "                                         ON     hip.HLTH_INSR_PLAN_SEQ =\n" +
            "                                                chi.HLTH_INSR_PLAN_SEQ\n" +
            "                                            AND hip.CRNT_REC_FLG = 1\n" +
            "                               WHERE chi.CRNT_REC_FLG = 1) hpln\n" +
            "                                 ON hpln.loan_app_seq = app.prnt_loan_app_seq\n" +
            "                       WHERE     app.loan_app_sts IN (702, 703)\n" +
            "                             AND app.clnt_seq = :clntseq\n" +
            "                             AND app.crnt_rec_flg = 1\n" +
            "                             AND amt IS NOT NULL\n" +
            "                    GROUP BY app.prnt_loan_app_seq,\n" +
            "                             TRUNC (psd.due_dt),\n" +
            "                             NVL (t.TYP_STR, PLN_DSCR),\n" +
            "                             psc.CHRG_TYPS_SEQ\n" +
            "                    ORDER BY TRUNC (psd.due_dt), app.prnt_loan_app_seq) chrg\n" +
            "                       ON     chrg.loan_app_seq = la.loan_app_seq\n" +
            "                          AND chrg.due_dt = psd.due_dt\n" +
            "             WHERE     la.clnt_seq = :clntseq\n" +
            "                   AND la.crnt_rec_flg = 1\n" +
            "                   AND la.loan_app_sts IN (702, 703)\n" +
            "                   AND la.PRD_SEQ = 51\n" +
            "          GROUP BY TRUNC (psd.due_dt),\n" +
            "                   CASE\n" +
            "                       WHEN la.PRD_SEQ IN (29, 4) THEN 'KKK <KSK>'\n" +
            "                       ELSE prd.PRD_CMNT\n" +
            "                   END,\n" +
            "                   CASE WHEN la.PRD_SEQ IN (29, 4) THEN 4 ELSE 51 END)\n" +
            "ORDER BY due_dt, PRD_CMNT";
    public static String BRANCH_INFO = "SELECT reg.reg_nm, area.area_nm,b.brnch_nm,b.brnch_cd,b.brnch_seq   from  mw_brnch b \r\n"
            + " join mw_brnch_emp_rel br on br.brnch_seq=b.brnch_seq and br.CRNT_REC_FLG=1 and br.del_flg=0 \r\n"
            + " join mw_emp emp on emp.emp_seq = br.emp_seq and emp.emp_lan_id =:userId \r\n"
            + " join mw_area area on area.area_seq=b.area_seq and area.crnt_rec_flg=1 \r\n"
            + " join mw_reg reg on reg.reg_seq=area.reg_seq and reg.crnt_rec_flg=1   where b.crnt_rec_flg=1\r\n" + " union \r\n"
            + "SELECT reg.reg_nm, area.area_nm,b.brnch_nm,b.brnch_cd,b.brnch_seq   from  mw_brnch b \r\n"
            + " join mw_port prt on prt.BRNCH_SEQ= b.BRNCH_SEQ and prt.CRNT_REC_FLG=1\r\n"
            + " join mw_port_emp_rel per on per.PORT_SEQ=prt.PORT_SEQ and per.CRNT_REC_FLG=1  \r\n"
            + " join mw_emp emp on emp.emp_seq = per.emp_seq and emp.emp_lan_id =:userId \r\n"
            + " join mw_area area on area.area_seq=b.area_seq and area.crnt_rec_flg=1 \r\n"
            + " join mw_reg reg on reg.reg_seq=area.reg_seq and reg.crnt_rec_flg=1   where b.crnt_rec_flg=1";
    public static String BRANCH_INFO_BRNCH_SEQ = "SELECT reg.reg_nm, area.area_nm,b.brnch_nm,b.brnch_cd,b.brnch_seq   from  mw_brnch b \r\n"
            + "             join mw_area area on area.area_seq=b.area_seq and area.crnt_rec_flg=1 \r\n"
            + "             join mw_reg reg on reg.reg_seq=area.reg_seq and reg.crnt_rec_flg=1   where b.crnt_rec_flg=1 and b.brnch_seq=:brnchSeq union \r\n"
            + "            SELECT reg.reg_nm, area.area_nm,b.brnch_nm,b.brnch_cd,b.brnch_seq   from  mw_brnch b\r\n"
            + "             join mw_area area on area.area_seq=b.area_seq and area.crnt_rec_flg=1 \r\n"
            + "             join mw_reg reg on reg.reg_seq=area.reg_seq and reg.crnt_rec_flg=1   where b.crnt_rec_flg=1  and b.brnch_seq=:brnchSeq";
    public static String AREA_INFO_BY_AREA_SEQ = "select rg.reg_nm,ar.area_nm from mw_area ar \r\n"
            + "            join mw_reg rg on rg.reg_seq=ar.reg_seq and rg.crnt_rec_flg=1 \r\n"
            + "            where ar.area_seq=:areaSeq and ar.crnt_rec_flg=1";

    /// Query for Client-Health-Beneficiary
    public static String AREA_INFO = "select rg.reg_nm,ar.area_nm\r\n" + "from mw_area_emp_rel arl\r\n"
            + "join mw_area ar on ar.area_seq=arl.area_seq and ar.crnt_rec_flg=1\r\n"
            + "join mw_emp emp on emp.emp_seq=arl.emp_seq and emp_lan_id=:userId\r\n"
            + "join mw_reg rg on rg.reg_seq=ar.reg_seq and rg.crnt_rec_flg=1\r\n" + "where arl.crnt_rec_flg=1\r\n";

    // Query for Insurance Claim
    public static String USER_BRANCH_INFO = "SELECT reg.reg_nm, area.area_nm,b.brnch_nm,b.brnch_cd\r\n" + " from mw_port p\r\n"
            + " join mw_brnch b on p.BRNCH_SEQ=b.BRNCH_SEQ and b.crnt_rec_flg=1\r\n"
            + " join mw_area area on area.area_seq=b.area_seq and area.crnt_rec_flg=1\r\n"
            + " join mw_reg reg on reg.reg_seq=area.reg_seq and reg.crnt_rec_flg=1\r\n" + " where p.PORT_SEQ=:portKey and p.crnt_rec_flg=1";
    public static String BRANCH_INFO_BY_BRANCH = "SELECT reg.reg_nm, area.area_nm,b.brnch_nm,b.brnch_cd,b.brnch_seq from  mw_brnch b \r\n"
            + " join mw_area area on area.area_seq=b.area_seq and area.crnt_rec_flg=1\r\n"
            + " join mw_reg reg on reg.reg_seq=area.reg_seq and reg.crnt_rec_flg=1\r\n"
            + " where b.BRNCH_SEQ=:brnchSeq and b.crnt_rec_flg=1";
    public static String CHARG_ORDER = "select adj_ordr,prcao.prd_chrg_seq,t.typ_seq,typ_str,t.gl_acct_num,app.prd_seq  \r\n"
            + "from mw_loan_app app\r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq=app.loan_app_sts and val.crnt_rec_flg=1 and (val.ref_cd = '0005' or val.ref_cd ='1245')\r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\r\n"
            + "join mw_loan_app_chrg_stngs pc on app.loan_app_seq=pc.loan_app_seq\r\n"
            + "join mw_prd_chrg_adj_ordr prcao on prcao.prd_chrg_seq=pc.prd_chrg_seq and prcao.crnt_rec_flg=1\r\n"
            + "left outer join mw_typs t on pc.typ_seq=t.typ_seq and t.crnt_rec_flg =1 \r\n"
            + "where app.crnt_rec_flg = 1 and app.clnt_seq=:clntSeq\r\n" + "union  \r\n"
            + "select adj_ordr,prcao.prd_chrg_seq,null,null,null,app.prd_seq\r\n" + "from mw_loan_app app\r\n"
            + "join mw_prd_chrg_adj_ordr prcao on prcao.prd_seq=app.prd_seq and prcao.crnt_rec_flg=1\r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq=app.loan_app_sts and val.crnt_rec_flg=1 and (val.ref_cd = '0005' or val.ref_cd ='1245')\r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\r\n"
            + "where prcao.crnt_rec_flg =1 and prcao.prd_chrg_seq<1 and  app.clnt_seq=:clntSeq and app.crnt_rec_flg = 1\r\n" + "union\r\n"
            + "select null adj_ordr,null prd_chrg_seq,t.typ_seq,t.typ_str,t.gl_acct_num,app.prd_seq \r\n" + "from mw_loan_app app \r\n"
            + "join mw_ref_cd_val val on val.ref_cd_seq=app.loan_app_sts and val.crnt_rec_flg=1 and (val.ref_cd = '0005' or val.ref_cd ='1245')\r\n"
            + "join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.crnt_rec_flg=1 and grp.ref_cd_grp = '0106'\r\n"
            + "join mw_dsbmt_vchr_hdr dvh on dvh.loan_app_seq=app.loan_app_seq and dvh.crnt_rec_flg=1\r\n"
            + "join mw_dsbmt_vchr_dtl dvd on dvh.dsbmt_hdr_seq=dvd.dsbmt_hdr_seq  \r\n"
            + "join mw_typs t on t.typ_seq=dvd.pymt_typs_seq and t.crnt_rec_flg=1 and t.typ_id='0010'  \r\n"
            + "where dvd.crnt_rec_flg=1 and dvh.dsbmt_vchr_typ=0 and app.clnt_seq=:clntSeq and app.crnt_rec_flg = 1 \r\n"
            + "order by prd_seq,adj_ordr asc,prd_seq nulls last";
    public static String REF_CD_VAL = "select ref_cd_seq" + " from mw_ref_cd_val val"
            + " join mw_ref_cd_grp grp on grp.ref_cd_grp_seq = val.ref_cd_grp_key and grp.CRNT_REC_FLG=1 and grp.ref_cd_grp = :refCdGrp"
            + " where val.CRNT_REC_FLG=1 and val.ref_cd =:refCd";
    public static String OVER_DUE_LOANS = "select prt.PORT_NM,emp.emp_nm,\r\n"
            + "clnt.clnt_id,clnt.frst_nm || ' ' || clnt.last_nm clnt_nm, \r\n"
            + "nvl(fthr_frst_nm,spz_frst_nm) ||' ' || nvl(fthr_last_nm,spz_last_nm) fs_nm, clnt.ph_num, \r\n"
            + "'St. '||adr.strt||' '||'H. ' ||adr.hse_num||' '||adr.oth_dtl||' '|| cty.city_nm addr, vhdr.dsbmt_dt dsbmt_dt, \r\n"
            + "ap.aprvd_loan_amt dsbmt_amt, loan_app_ost(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'ri') ost_inst_num, \r\n"
            + "loan_app_ost(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'p') ost_inst_prncp, \r\n"
            + "loan_app_ost(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'s') ost_inst_srvc, \r\n"
            + "get_od_info(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'i') od_inst_num, \r\n"
            + "get_od_info(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'ps') od_amt, \r\n"
            + "get_od_info(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'d') od_days,\r\n"
            + "(select max(pymt_dt) from mw_rcvry_trx trx where trx.crnt_rec_flg=1 and trx.pymt_ref=clnt.clnt_seq) cmp_dt,chk.ref_cd_dscr rsn,\r\n"
            + "chk.cmnt\r\n" + "from mw_clnt clnt \r\n"
            + "join mw_addr_rel adrl on adrl.enty_key=clnt.clnt_seq and adrl.enty_typ='Client' and adrl.crnt_rec_flg=1 \r\n"
            + "join mw_addr adr on adr.addr_seq=adrl.addr_seq and adr.crnt_rec_flg=1 \r\n"
            + "join mw_city_uc_rel ucrl on ucrl.city_uc_rel_seq=adr.city_seq and ucrl.crnt_rec_flg=1 \r\n"
            + "join mw_city cty on cty.city_seq=ucrl.city_seq and cty.crnt_rec_flg=1 \r\n"
            + "join mw_port prt on prt.port_seq=clnt.port_key and prt.crnt_rec_flg=1 \r\n"
            + "join mw_port_emp_rel erl on erl.port_seq=prt.port_seq and erl.crnt_rec_flg=1 \r\n"
            + "join mw_emp emp on emp.emp_seq=erl.emp_seq \r\n"
            + "join mw_loan_app ap on ap.clnt_seq=clnt.clnt_seq and ap.crnt_rec_flg=1 \r\n"
            + "join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1 \r\n"
            + "join mw_dsbmt_vchr_hdr vhdr on vhdr.loan_app_seq = ap.loan_app_seq and vhdr.crnt_rec_flg=1 and vhdr.DSBMT_VCHR_TYP=0\r\n"
            + "join mw_pymt_sched_hdr phdr on phdr.loan_app_seq=ap.loan_app_seq and phdr.crnt_rec_flg=1 \r\n"
            + "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1 \r\n"
            + "left outer join (select chks.loan_app_seq,rsn.ref_cd_dscr,chks.cmnt\r\n" + "    from MW_LOAN_APP_MNTRNG_CHKS chks\r\n"
            + "    join mw_ref_cd_val rsn on rsn.ref_cd_seq=chks.rsn and rsn.crnt_rec_flg=1\r\n"
            + "    where chks.crnt_rec_flg=1) chk on chk.loan_app_seq=ap.loan_app_seq\r\n" + "where clnt.crnt_rec_flg=1 \r\n"
            + "and lsts.ref_cd='0005'\r\n" + "and prd.PRD_GRP_SEQ=:prdSeq \r\n"
            + "and get_od_info(ap.loan_app_seq,to_date(:aDt,'dd-MM-yyyy'),'ps') > 0\r\n" + "and prt.brnch_seq = :brnch \r\n"
            + "order by emp_nm,od_days desc";
    public static String PORTFOLIO_MONITORING = "select grp.prd_grp_nm,emp.emp_nm,erl.eff_start_dt, \r\n"
            + "sum(nvl(ost_clnt,0)) ost_clnt, sum(nvl(ost_amt,0)) ost_amt, \r\n"
            + "sum(nvl(due_perd_clnt,0)) due_perd_clnt, sum(nvl(due_perd_amt,0)) due_perd_amt, \r\n"
            + "sum(nvl(rcvrd_clnt,0)) rcvrd_clnt, sum(nvl(rcvrd_amt,0)) rcvrd_amt,\r\n" + "round\r\n"
            + "(max(nvl(tgt.trgt_clients,0)) / \r\n" + "nvl((\r\n" + "select count(distinct mp.port_seq) prt\r\n"
            + "        from mw_loan_app ap\r\n"
            + "        join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1,\r\n"
            + "        mw_port mp, mw_dsbmt_vchr_hdr dvh\r\n"
            + "        where ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= :todt and ap.crnt_rec_flg=1) \r\n"
            + "        or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > :todt and to_date(dvh.dsbmt_dt) <= :todt)              \r\n"
            + "        or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > :todt)) \r\n"
            + "        and loan_app_ost(ap.loan_app_seq,:todt) > 0\r\n" + "        and ap.port_seq = mp.port_seq\r\n"
            + "        and dvh.loan_app_seq = ap.loan_app_seq    \r\n"
            + "        and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= :todt)    \r\n"
            + "        and dvh.crnt_rec_flg = 1\r\n" + "        and ap.crnt_rec_flg = 1\r\n" + "        and mp.crnt_rec_flg = 1\r\n"
            + "        and mp.brnch_seq = :branch \r\n" + "),1)) trgt_clnt,\r\n" + "sum(nvl(achvd_in_perd,0)) achvd_in_perd, \r\n"
            + "round \r\n" + "(max(nvl(tgt.trgt_amt,0)) / \r\n" + "nvl((\r\n" + "select count(distinct mp.port_seq) prt\r\n"
            + "        from mw_loan_app ap\r\n"
            + "        join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1,\r\n"
            + "        mw_port mp, mw_dsbmt_vchr_hdr dvh\r\n"
            + "        where ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= :todt and ap.crnt_rec_flg=1) \r\n"
            + "        or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > :todt and to_date(dvh.dsbmt_dt) <= :todt)              \r\n"
            + "        or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > :todt)) \r\n"
            + "        and loan_app_ost(ap.loan_app_seq,:todt) > 0\r\n" + "        and ap.port_seq = mp.port_seq\r\n"
            + "        and dvh.loan_app_seq = ap.loan_app_seq    \r\n"
            + "        and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= :todt)    \r\n"
            + "        and dvh.crnt_rec_flg = 1\r\n" + "        and ap.crnt_rec_flg = 1\r\n" + "        and mp.crnt_rec_flg = 1\r\n"
            + "        and mp.brnch_seq = :branch \r\n" + "),1)) trgt_amt, \r\n" + "sum(nvl(achvd_in_perd_amt,0))achvd_in_perd_amt, \r\n"
            + "sum(nvl(od_clnt,0)) od_clnt, sum(nvl(od_amt,0)) od_amt, \r\n"
            + "sum(nvl(par_1_dy_cnt,0)) par_1_dy_cnt, sum(nvl(par_1_dy_amt,0)) par_1_dy_amt, \r\n"
            + "sum(nvl(par_30_day_cnt,0)) par_30_day_cnt, sum(nvl(par_30_day_amt,0)) par_30_day_amt, \r\n"
            + "sum(nvl(cmpltd_loans,0)) cmpltd_loans, sum(nvl(od_bp_clnt,0)) od_bp_clnt, \r\n"
            + "sum(nvl(od_bp_amt,0)) od_bp_amt0 from mw_brnch brnch \r\n"
            + "join mw_port prt on prt.brnch_seq=brnch.brnch_seq and prt.crnt_rec_flg=1 \r\n"
            + "join mw_port_emp_rel erl on erl.port_seq=prt.port_seq and erl.crnt_rec_flg=1 \r\n"
            + "join mw_emp emp on emp.emp_seq=erl.emp_seq \r\n"
            + "join mw_brnch_prd_rel prl on prl.brnch_seq=brnch.brnch_seq and prl.crnt_rec_flg=1 \r\n"
            + "join mw_prd prd on prd.prd_seq=prl.prd_seq and prd.crnt_rec_flg=1 \r\n"
            + "join mw_prd_grp grp on grp.prd_grp_seq=prd.prd_grp_seq and grp.crnt_rec_flg=1 left outer join ( \r\n"
            + "select ap.prd_seq, ap.port_seq, count(ap.loan_app_seq) ost_clnt, \r\n"
            + "sum(loan_app_ost(ap.loan_app_seq,:todt)) ost_amt from mw_loan_app ap \r\n"
            + "join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1 \r\n"
            + "join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1 and prt.brnch_seq=:branch,\r\n"
            + "mw_dsbmt_vchr_hdr dvh\r\n"
            + "where ( (asts.ref_cd='0005' and to_date(ap.loan_app_sts_dt) <= :todt and ap.crnt_rec_flg=1) \r\n"
            + "or (asts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) > :todt and to_date(dvh.dsbmt_dt) <= :todt) \r\n"
            + "or (asts.ref_cd='1245' and to_date(ap.loan_app_sts_dt) > :todt))\r\n" + "and dvh.loan_app_seq = ap.loan_app_seq \r\n"
            + "and dvh.crnt_rec_flg = 1\r\n"
            + "and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= :todt)\r\n"
            + "group by ap.prd_seq,ap.port_seq \r\n" + ") ost on ost.prd_seq=prd.prd_seq and ost.port_seq=prt.port_seq \r\n"
            + "left outer join  ( select ap.prd_seq,ap.port_seq, count(ap.loan_app_seq) DUE_PERD_CLNT, \r\n"
            + "sum(psd.PPAL_AMT_DUE + nvl(psd.TOT_CHRG_DUE,0)) DUE_PERD_AMT from mw_loan_app ap \r\n"
            + "join mw_ref_cd_val asts on asts.ref_cd_seq=ap.loan_app_sts and asts.crnt_rec_flg=1 \r\n"
            + "join mw_pymt_sched_hdr psh on psh.loan_app_seq=ap.loan_app_seq and psh.crnt_rec_flg=1 \r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
            + "join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1 where ap.crnt_rec_flg=1 \r\n"
            + "and to_date(psd.due_dt) between to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy') \r\n"
            + "and (psd.PYMT_STS_KEY in (select vl.ref_cd_seq from mw_ref_cd_val vl where vl.ref_cd in ('0945')) \r\n"
            + "or (psd.PYMT_STS_KEY in (select vl.ref_cd_seq from mw_ref_cd_val vl where vl.ref_cd in ('0946','0947','0948','1145')) \r\n"
            + "and (select max(trx.pymt_dt) from mw_rcvry_trx trx, mw_rcvry_dtl dtl \r\n"
            + "where trx.RCVRY_TRX_SEQ = dtl.RCVRY_TRX_SEQ and dtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq \r\n"
            + "and dtl.crnt_rec_flg=1 and trx.crnt_rec_flg=1 ) >= to_date(:frmdt,'dd-MM-yyyy'))) group by ap.prd_seq,ap.port_seq \r\n"
            + ") dip on dip.prd_seq=prd.prd_seq and dip.port_seq=prt.port_seq left outer join (select prd_seq, port_seq,\r\n"
            + "        count(distinct LOAN_APP_SEQ) rcvrd_clnt, \r\n" + "        (sum(rec_pr) + sum(rec_sc)) rcvrd_amt\r\n"
            + "        from\r\n" + "        (\r\n" + "        select ap.LOAN_APP_SEQ, ap.prd_seq, ap.port_seq,\r\n"
            + "            nvl(sum((\r\n" + "                select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_trx rht ,mw_rcvry_dtl rdtl\r\n"
            + "                left outer join mw_typs rtyp on rtyp.typ_seq=rdtl.chrg_typ_key and rtyp.crnt_rec_flg=1             \r\n"
            + "                where rdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg=1\r\n"
            + "                and rht.RCVRY_TRX_SEQ = rdtl.RCVRY_TRX_SEQ and rht.crnt_rec_flg=1\r\n"
            + "                and (rdtl.chrg_typ_key=-1)   \r\n"
            + "                and rht.PYMT_DT BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')\r\n"
            + "            )),0) rec_pr,\r\n" + "            nvl(sum((\r\n"
            + "                select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_trx rht ,mw_rcvry_dtl rdtl\r\n"
            + "                left outer join mw_typs rtyp on rtyp.typ_seq=rdtl.chrg_typ_key and rtyp.crnt_rec_flg=1             \r\n"
            + "                where rdtl.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq and rdtl.crnt_rec_flg=1\r\n"
            + "                and rht.RCVRY_TRX_SEQ = rdtl.RCVRY_TRX_SEQ and rht.crnt_rec_flg=1\r\n"
            + "                and (rdtl.chrg_typ_key in (select mt.typ_seq from mw_typs mt where mt.typ_id = '0017' and mt.crnt_rec_flg = 1))   \r\n"
            + "                and rht.PYMT_DT BETWEEN to_date(:frmdt,'dd-MM-yyyy') and to_date(:todt,'dd-MM-yyyy')\r\n"
            + "            )),0) rec_sc\r\n" + "            --nto la, ost\r\n" + "            from mw_loan_app ap\r\n"
            + "            join mw_pymt_sched_hdr psh on psh.loan_app_seq=ap.loan_app_seq and psh.crnt_rec_flg=1\r\n"
            + "            join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1,\r\n"
            + "            mw_port mp\r\n" + "            where ap.PORT_SEQ = mp.PORT_SEQ\r\n" + "            and ap.crnt_rec_flg=1\r\n"
            + "            and not exists (select distinct ctl.LOAN_APP_SEQ from MW_CLNT_TAG_LIST ctl where ctl.tags_seq = 4 \r\n"
            + "            and ctl.loan_app_seq = ap.LOAN_APP_SEQ and to_date(ctl.EFF_START_DT) <= to_date(:todt,'dd-MM-yyyy'))                  \r\n"
            + "            group by ap.prd_seq, ap.port_seq, ap.loan_app_seq\r\n" + "    )\r\n"
            + "    where (rec_pr > 0 or rec_sc > 0) \r\n"
            + "    group by prd_seq, port_seq) rcvd on rcvd.prd_seq=prd.prd_seq and rcvd.port_seq=prt.port_seq \r\n"
            + "left outer join (select trgt.prd_seq, trgt.brnch_seq,\r\n" + "trgt.trgt_clients, trgt.trgt_amt\r\n"
            + "from mw_brnch_trgt trgt\r\n" + "where trgt.del_flg = 0 \r\n" + "and trgt.brnch_seq = :branch\r\n"
            + "and trgt.prd_seq != 8\r\n" + "and trgt.trgt_perd = to_char(to_date(:todt),'YYYYMM')\r\n"
            + ") tgt on tgt.prd_seq=grp.prd_grp_seq and tgt.brnch_seq=brnch.brnch_seq left outer join ( \r\n"
            + "select ap.prd_seq,ap.port_seq,count(distinct ap.loan_app_seq) achvd_in_perd, \r\n"
            + "sum(ap.aprvd_loan_amt) achvd_in_perd_amt \r\n" + "from mw_loan_app ap, mw_dsbmt_vchr_hdr dvh\r\n"
            + "where ap.crnt_rec_flg=1 \r\n" + "and ap.loan_app_seq = dvh.loan_app_seq\r\n" + "and dvh.crnt_rec_flg=1 \r\n"
            + "and dvh.crnt_rec_flg=1 \r\n" + "and to_date(dvh.dsbmt_dt) between :frmdt and :todt \r\n"
            + "group by ap.prd_seq,ap.port_seq) achvd on achvd.prd_seq=prd.prd_seq and achvd.port_seq=prt.port_seq \r\n"
            + "left outer join ( \r\n" + "select shld_rec.prd_seq,shld_rec.port_seq, \r\n"
            + "count(shld_rec.loan_app_seq) od_clnt, sum( (pr_due)+(sc_due)) od_amt \r\n" + "from (\r\n"
            + "select ap.loan_app_seq,ap.prd_seq,ap.port_seq, \r\n"
            + "nvl(sum(psd.ppal_amt_due),0) - sum(nvl((select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_dtl rdtl, mw_rcvry_trx trx\r\n"
            + "                            where trx.rcvry_trx_seq = rdtl.rcvry_trx_seq\r\n"
            + "                            and trx.crnt_rec_flg = 1\r\n"
            + "                            and to_date(trx.pymt_dt) <= :todt\r\n"
            + "                            and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq \r\n"
            + "                            and rdtl.crnt_rec_flg=1 and rdtl.chrg_typ_key = -1                            \r\n"
            + "                            group by rdtl.pymt_sched_dtl_seq),0)) pr_due, \r\n"
            + "nvl(sum(tot_chrg_due),0) - sum(nvl((select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_dtl rdtl , mw_rcvry_trx trx\r\n"
            + "                            where trx.rcvry_trx_seq = rdtl.rcvry_trx_seq\r\n"
            + "                            and trx.crnt_rec_flg = 1\r\n"
            + "                            and to_date(trx.pymt_dt) <= :todt\r\n"
            + "                            and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq  \r\n"
            + "                            and rdtl.crnt_rec_flg=1 and rdtl.chrg_typ_key in (416,413,418,419,383,414,17,415,417,412,410,411)\r\n"
            + "                            group by rdtl.pymt_sched_dtl_seq),0)) sc_due, \r\n"
            + "nvl(sum((select sum(amt) from mw_pymt_sched_chrg psc where psc.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq and crnt_rec_flg=1)),0) - \r\n"
            + "sum(nvl((select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_dtl rdtl , mw_rcvry_trx trx\r\n"
            + "                            where trx.rcvry_trx_seq = rdtl.rcvry_trx_seq\r\n"
            + "                            and trx.crnt_rec_flg = 1\r\n"
            + "                            and to_date(trx.pymt_dt) <= :todt\r\n"
            + "                            and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq \r\n"
            + "                            and rdtl.crnt_rec_flg=1 and rdtl.chrg_typ_key not in (-1,416,413,418,419,383,414,17,415,417,412,410,411)\r\n"
            + "                            group by rdtl.pymt_sched_dtl_seq),0)) chrg_due, \r\n"
            + "max(inst_num) inst_num from mw_loan_app ap \r\n"
            + "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1 \r\n"
            + "join mw_pymt_sched_hdr psh on psh.loan_app_seq= ap.loan_app_seq and psh.crnt_rec_flg=1 \r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
            + "join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1 and psd.due_dt <= :todt\r\n"
            + "and (psd.pymt_sts_key in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945') and ref_cd_grp_key = 179 and val.crnt_rec_flg=1) \r\n"
            + "or (psd.pymt_sts_key in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948','1145') ) \r\n"
            + "and ( select max(trx.pymt_dt) \r\n"
            + "from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1 \r\n"
            + "and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq) > :todt ) ) and ap.crnt_rec_flg =1 \r\n"
            + "and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= :todt)\r\n"
            + "group by ap.loan_app_seq,ap.prd_seq,ap.port_seq\r\n" + ")shld_rec\r\n"
            + "where ((pr_due) > 0 or (sc_due) > 0 or (chrg_due) > 0) \r\n" + "group by shld_rec.prd_seq,shld_rec.port_seq \r\n"
            + ") od   \r\n" + "on od.prd_seq=prd.prd_seq and od.port_seq=prt.port_seq\r\n" + "left outer join \r\n" + "(   \r\n"
            + "select shld_rec.prd_seq,shld_rec.port_seq, \r\n" + "count(shld_rec.loan_app_seq) par_1_dy_cnt, \r\n"
            + "sum(shld_rec.ost) par_1_dy_amt,\r\n"
            + "count(distinct (case when to_date(:todt) - due_dt > 30 then (shld_rec.loan_app_seq) end)) par_30_day_cnt,\r\n"
            + "nvl(sum(case when to_date(:todt) - due_dt > 30 then shld_rec.ost end),0) par_30_day_amt\r\n" + "from (\r\n"
            + "select ap.loan_app_seq,ap.prd_seq,ap.port_seq, min(psd.due_dt) due_dt,\r\n" + "loan_app_ost(ap.loan_app_seq,:todt) ost,\r\n"
            + "nvl(sum(psd.ppal_amt_due),0) - sum(nvl((select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_dtl rdtl, mw_rcvry_trx trx\r\n"
            + "                            where trx.rcvry_trx_seq = rdtl.rcvry_trx_seq\r\n"
            + "                            and trx.crnt_rec_flg = 1\r\n"
            + "                            and to_date(trx.pymt_dt) <= :todt\r\n"
            + "                            and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq \r\n"
            + "                            and rdtl.crnt_rec_flg=1 and rdtl.chrg_typ_key = -1                            \r\n"
            + "                            group by rdtl.pymt_sched_dtl_seq),0)) pr_due, \r\n"
            + "nvl(sum(tot_chrg_due),0) - sum(nvl((select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_dtl rdtl , mw_rcvry_trx trx\r\n"
            + "                            where trx.rcvry_trx_seq = rdtl.rcvry_trx_seq\r\n"
            + "                            and trx.crnt_rec_flg = 1\r\n"
            + "                            and to_date(trx.pymt_dt) <= :todt\r\n"
            + "                            and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq  \r\n"
            + "                            and rdtl.crnt_rec_flg=1 and rdtl.chrg_typ_key in (416,413,418,419,383,414,17,415,417,412,410,411)\r\n"
            + "                            group by rdtl.pymt_sched_dtl_seq),0)) sc_due, \r\n"
            + "nvl(sum((select sum(amt) from mw_pymt_sched_chrg psc where psc.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq and crnt_rec_flg=1)),0) - \r\n"
            + "sum(nvl((select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_dtl rdtl , mw_rcvry_trx trx\r\n"
            + "                            where trx.rcvry_trx_seq = rdtl.rcvry_trx_seq\r\n"
            + "                            and trx.crnt_rec_flg = 1\r\n"
            + "                            and to_date(trx.pymt_dt) <= :todt\r\n"
            + "                            and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq \r\n"
            + "                            and rdtl.crnt_rec_flg=1 and rdtl.chrg_typ_key not in (-1,416,413,418,419,383,414,17,415,417,412,410,411)\r\n"
            + "                            group by rdtl.pymt_sched_dtl_seq),0)) chrg_due, \r\n"
            + "max(inst_num) inst_num from mw_loan_app ap \r\n"
            + "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1 \r\n"
            + "join mw_pymt_sched_hdr psh on psh.loan_app_seq= ap.loan_app_seq and psh.crnt_rec_flg=1 \r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
            + "join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1 and psd.due_dt <= :todt\r\n"
            + "and (psd.pymt_sts_key in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945') and ref_cd_grp_key = 179 and val.crnt_rec_flg=1) \r\n"
            + "or (psd.pymt_sts_key in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948','1145') ) \r\n"
            + "and ( select max(trx.pymt_dt) \r\n"
            + "from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1 \r\n"
            + "and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq) > :todt ) ) and ap.crnt_rec_flg =1 \r\n"
            + "and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= :todt)\r\n"
            + "group by ap.loan_app_seq,ap.prd_seq,ap.port_seq\r\n" + ")shld_rec\r\n"
            + "where ((pr_due) > 0 or (sc_due) > 0 or (chrg_due) > 0) \r\n" + "group by shld_rec.prd_seq,shld_rec.port_seq \r\n"
            + ") par   \r\n" + "on par.prd_seq=prd.prd_seq and par.port_seq=prt.port_seq left outer join \r\n" + "( \r\n"
            + "select ap.prd_seq,ap.port_seq,count(ap.loan_app_seq) cmpltd_loans from mw_loan_app ap \r\n"
            + "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1 where ap.crnt_rec_flg=1 \r\n"
            + "and lsts.ref_cd='0006' and to_date(ap.loan_app_sts_dt) between :frmdt and :todt group by ap.prd_seq,ap.port_seq \r\n"
            + ") cmpltd \r\n" + "on cmpltd.prd_seq=prd.prd_seq and cmpltd.port_seq=prt.port_seq left outer join \r\n" + "( \r\n"
            + "select shld_rec.prd_seq,shld_rec.port_seq, \r\n"
            + "count(shld_rec.loan_app_seq) od_bp_clnt, sum( (pr_due)+(sc_due)) od_bp_amt \r\n" + "from (\r\n"
            + "select ap.loan_app_seq,ap.prd_seq,ap.port_seq, \r\n"
            + "nvl(sum(psd.ppal_amt_due),0) - sum(nvl((select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_dtl rdtl, mw_rcvry_trx trx\r\n"
            + "                            where trx.rcvry_trx_seq = rdtl.rcvry_trx_seq\r\n"
            + "                            and trx.crnt_rec_flg = 1\r\n"
            + "                            and to_date(trx.pymt_dt) <= to_date(:frmdt)-1\r\n"
            + "                            and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq \r\n"
            + "                            and rdtl.crnt_rec_flg=1 and rdtl.chrg_typ_key = -1                            \r\n"
            + "                            group by rdtl.pymt_sched_dtl_seq),0)) pr_due, \r\n"
            + "nvl(sum(tot_chrg_due),0) - sum(nvl((select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_dtl rdtl , mw_rcvry_trx trx\r\n"
            + "                            where trx.rcvry_trx_seq = rdtl.rcvry_trx_seq\r\n"
            + "                            and trx.crnt_rec_flg = 1\r\n"
            + "                            and to_date(trx.pymt_dt) <= to_date(:frmdt)-1\r\n"
            + "                            and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq  \r\n"
            + "                            and rdtl.crnt_rec_flg=1 and rdtl.chrg_typ_key in (416,413,418,419,383,414,17,415,417,412,410,411)\r\n"
            + "                            group by rdtl.pymt_sched_dtl_seq),0)) sc_due, \r\n"
            + "nvl(sum((select sum(amt) from mw_pymt_sched_chrg psc where psc.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq and crnt_rec_flg=1)),0) - \r\n"
            + "sum(nvl((select sum(nvl(rdtl.pymt_amt,0)) from mw_rcvry_dtl rdtl , mw_rcvry_trx trx\r\n"
            + "                            where trx.rcvry_trx_seq = rdtl.rcvry_trx_seq\r\n"
            + "                            and trx.crnt_rec_flg = 1\r\n"
            + "                            and to_date(trx.pymt_dt) <= to_date(:frmdt)-1\r\n"
            + "                            and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq \r\n"
            + "                            and rdtl.crnt_rec_flg=1 and rdtl.chrg_typ_key not in (-1,416,413,418,419,383,414,17,415,417,412,410,411)\r\n"
            + "                            group by rdtl.pymt_sched_dtl_seq),0)) chrg_due, \r\n"
            + "max(inst_num) inst_num from mw_loan_app ap \r\n"
            + "join mw_ref_cd_val lsts on lsts.ref_cd_seq=ap.loan_app_sts and lsts.crnt_rec_flg=1 \r\n"
            + "join mw_pymt_sched_hdr psh on psh.loan_app_seq= ap.loan_app_seq and psh.crnt_rec_flg=1 \r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1 \r\n"
            + "join mw_ref_cd_val vl on vl.ref_cd_seq=psd.pymt_sts_key and vl.crnt_rec_flg=1 and psd.due_dt <= to_date(:frmdt)-1\r\n"
            + "and (psd.pymt_sts_key in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0945') and ref_cd_grp_key = 179 and val.crnt_rec_flg=1) \r\n"
            + "or (psd.pymt_sts_key in (select val.ref_cd_seq from mw_ref_cd_val val where val.ref_cd in ('0948','1145') ) \r\n"
            + "and ( select max(trx.pymt_dt) \r\n"
            + "from mw_rcvry_dtl rdtl join mw_rcvry_trx trx on trx.rcvry_trx_seq=rdtl.rcvry_trx_seq and trx.crnt_rec_flg=1 \r\n"
            + "and rdtl.pymt_sched_dtl_seq = psd.pymt_sched_dtl_seq) > to_date(:frmdt)-1 ) ) and ap.crnt_rec_flg =1 \r\n"
            + "and not exists (select distinct ctl.loan_app_seq from mw_clnt_tag_list ctl where ctl.loan_app_seq = ap.loan_app_seq and ctl.eff_start_dt <= to_date(:frmdt)-1)\r\n"
            + "group by ap.loan_app_seq,ap.prd_seq,ap.port_seq\r\n" + ")shld_rec\r\n"
            + "where ((pr_due) > 0 or (sc_due) > 0 or (chrg_due) > 0) \r\n" + "group by shld_rec.prd_seq,shld_rec.port_seq\r\n"
            + ") pbp  \r\n" + "on pbp.prd_seq=prd.prd_seq and pbp.port_seq=prt.port_seq \r\n" + "where prt.brnch_seq=:branch \r\n"
            + "and (nvl(ost_clnt,0)+ nvl(ost_amt,0)+ nvl(due_perd_clnt,0)+ nvl(due_perd_amt,0)+ nvl(rcvrd_clnt,0)+ nvl(rcvrd_amt,0)+ nvl(achvd_in_perd,0)+ nvl(achvd_in_perd_amt,0)+ \r\n"
            + "nvl(od_clnt,0)+ nvl(od_amt,0)+ nvl(par_1_dy_cnt,0)+ nvl(par_1_dy_amt,0)+ nvl(par_30_day_cnt,0)+ nvl(par_30_day_amt,0)+nvl(cmpltd_loans,0)) > 0 \r\n"
            + "group by grp.prd_grp_seq, grp.prd_grp_nm,emp.emp_nm,erl.eff_start_dt order by grp.prd_grp_seq,emp.emp_nm";
    public static String CLT_HEALTH_BENEFICIARY = "select distinct la.CLNT_SEQ,\r\n"
            + "    initcap(c.FRST_NM||' '||c.LAST_NM) client_name,\r\n" + "    (c.SPZ_FRST_NM||' '||c.SPZ_LAST_NM) husband_name,\r\n"
            + "    (c.FTHR_FRST_NM||' '||c.FTHR_LAST_NM) father_name,\r\n" + "    c.CNIC_NUM,\r\n"
            + "    initcap(pln.PLAN_NM) plan,pln.ANL_PREM_AMT,\r\n" + "    la.CLNT_SEQ HLTH_INSR_MEMB_SEQ,\r\n"
            + "    initcap(c.FRST_NM||' '||c.LAST_NM) MEMBER_NM,\r\n"
            + "    trunc(months_between(to_date(ldm.DSBMT_DT),TO_DATE(TO_CHAR(c.DOB,'DDMMYYYY'),'DDMMYYYY'))/12)||' Years '||trunc(mod(months_between(to_date(ldm.DSBMT_DT),TO_DATE(TO_CHAR(c.DOB,'DDMMYYYY'),'DDMMYYYY')),12))||' Months '|| trunc(sysdate-add_months(TO_DATE(TO_CHAR(to_date(ldm.DSBMT_DT),'DDMMYYYY'),'DDMMYYYY'),trunc(months_between(sysdate,TO_DATE(TO_CHAR(to_date(ldm.DSBMT_DT),'DDMMYYYY'),'DDMMYYYY'))/12)*12+trunc(mod(months_between(sysdate,TO_DATE(TO_CHAR(to_date(ldm.DSBMT_DT),'DDMMYYYY'),'DDMMYYYY')),12))))||' Days' Age_at_insurace_time,\r\n"
            + "    (select initcap(val.REF_CD_DSCR) from MW_REF_CD_VAL val where val.REF_CD_SEQ = c.MRTL_STS_KEY and val.CRNT_REC_FLG = 1 )marital_status,\r\n"
            + "    'Self' relation,\r\n"
            + "    (select initcap(val.REF_CD_DSCR) from MW_REF_CD_VAL val where val.REF_CD_SEQ = c.GNDR_KEY and val.CRNT_REC_FLG = 1 )Gender,\r\n"
            + "    trunc(to_date(ldm.EFF_START_DT))enrollment_date,\r\n" + "    trunc(to_date(ldm.DSBMT_DT))insurance_date,\r\n"
            + "    trunc(to_date(ldm.DSBMT_DT)+365) maturity_date,\r\n" + "    hl.CARD_NUM,\r\n"
            + "    (select dt_of_dth from mw_dth_rpt dr where dr.crnt_rec_flg=1 and dr.clnt_seq=c.clnt_seq) dth_dt\r\n" + "from\r\n"
            + "mw_loan_app la,mw_clnt c,\r\n" + "MW_CLNT_HLTH_INSR_CARD hl,MW_HLTH_INSR_PLAN pln,\r\n"
            + "MW_CLNT_HLTH_INSR ins,MW_DSBMT_VCHR_HDR ldm,mw_port mp\r\n" + "where la.CLNT_SEQ = c.CLNT_SEQ and la.CRNT_REC_FLG = 1\r\n"
            + "and la.LOAN_APP_STS = 703 and c.CRNT_REC_FLG = 1\r\n" + "and la.PORT_SEQ = mp.PORT_SEQ\r\n"
            + "and la.LOAN_APP_SEQ = hl.LOAN_APP_SEQ\r\n" + "and hl.LOAN_APP_SEQ = ins.LOAN_APP_SEQ\r\n"
            + "and ins.HLTH_INSR_PLAN_SEQ = pln.HLTH_INSR_PLAN_SEQ\r\n"
            + "and la.LOAN_APP_SEQ = ldm.LOAN_APP_SEQ and ldm.CRNT_REC_FLG = 1\r\n" + "and mp.BRNCH_SEQ = :brnch_cd\r\n"
            + "and to_date(ldm.DSBMT_DT) between to_date(:from_dt) and to_date(:to_dt)\r\n" + "UNION\r\n"
            + "SELECT distinct la.CLNT_SEQ,initcap(c.FRST_NM||' '||c.LAST_NM) client_name,\r\n"
            + "(select distinct nvl(initcap(rel1.FRST_NM||' '||rel1.LAST_NM),'') from mw_clnt_rel rel1 join mw_ref_cd_val val on rel1.rel_wth_clnt_key=val.ref_cd_seq and val.crnt_rec_flg=1 and ref_cd='0008'\r\n"
            + "       where rel1.loan_app_seq = la.loan_app_seq and rel1.CRNT_REC_FLG = 1\r\n" + ")husband_name,\r\n"
            + "(select nvl(initcap(rel1.FRST_NM||' '||rel1.LAST_NM),'') from mw_clnt_rel rel1 join mw_ref_cd_val val on rel1.rel_wth_clnt_key=val.ref_cd_seq and val.crnt_rec_flg=1 and ref_cd='0001'\r\n"
            + "where rel1.loan_app_seq = la.loan_app_seq and rel1.CRNT_REC_FLG = 1\r\n" + ")father_name,\r\n" + "c.CNIC_NUM,\r\n"
            + "initcap(pln.PLAN_NM) plan,pln.ANL_PREM_AMT,\r\n" + "ins.CLNT_HLTH_INSR_SEQ HLTH_INSR_MEMB_SEQ,\r\n"
            + "initcap(rel.FRST_NM||' '||rel.LAST_NM) MEMBER_NM,\r\n"
            + "trunc(months_between(to_date(ldm.DSBMT_DT),TO_DATE(TO_CHAR(rel.DOB,'DDMMYYYY'),'DDMMYYYY'))/12)||' Years '||trunc(mod(months_between(to_date(ldm.DSBMT_DT),TO_DATE(TO_CHAR(rel.DOB,'DDMMYYYY'),'DDMMYYYY')),12))||' Months '|| trunc(sysdate-add_months(TO_DATE(TO_CHAR(to_date(ldm.DSBMT_DT),'DDMMYYYY'),'DDMMYYYY'),trunc(months_between(sysdate,TO_DATE(TO_CHAR(to_date(ldm.DSBMT_DT),'DDMMYYYY'),'DDMMYYYY'))/12)*12+trunc(mod(months_between(sysdate,TO_DATE(TO_CHAR(to_date(ldm.DSBMT_DT),'DDMMYYYY'),'DDMMYYYY')),12))))||' Days' Age_at_insurace_time,\r\n"
            + "(select initcap(val.REF_CD_DSCR) from MW_REF_CD_VAL val where val.REF_CD_SEQ = c.MRTL_STS_KEY and val.CRNT_REC_FLG = 1 ) marital_status,\r\n"
            + "(select initcap(val.REF_CD_DSCR) from MW_REF_CD_VAL val where val.REF_CD_SEQ = rel.REL_WTH_CLNT_KEY and val.CRNT_REC_FLG = 1 )relation,\r\n"
            + "(select initcap(val.REF_CD_DSCR) from MW_REF_CD_VAL val where val.REF_CD_SEQ = rel.GNDR_KEY and val.CRNT_REC_FLG = 1 )Gender,\r\n"
            + "trunc(to_date(ldm.EFF_START_DT))enrollment_date,\r\n" + "trunc(to_date(ldm.DSBMT_DT))insurance_date,\r\n"
            + "trunc(to_date(ldm.DSBMT_DT)+365) maturity_date,\r\n" + "hl.CARD_NUM,\r\n"
            + "(select dt_of_dth from mw_dth_rpt dr where dr.crnt_rec_flg=1 and dr.clnt_seq=c.clnt_seq) dth_dt\r\n" + "from\r\n"
            + "mw_loan_app la,mw_clnt_rel rel,mw_clnt c,\r\n" + "MW_CLNT_HLTH_INSR_CARD hl,MW_HLTH_INSR_PLAN pln,\r\n"
            + "MW_CLNT_HLTH_INSR ins,MW_DSBMT_VCHR_HDR ldm,mw_port mp\r\n"
            + "where la.LOAN_APP_SEQ = rel.loan_app_seq and la.CRNT_REC_FLG = 1\r\n"
            + "and la.CLNT_SEQ = c.CLNT_SEQ and c.CRNT_REC_FLG = 1 and rel.CRNT_REC_FLG = 1\r\n"
            + "and rel.REL_WTH_CLNT_KEY in (421,584,440,545,567,17,966,188,556,1005,434)\r\n" + "and la.LOAN_APP_SEQ = hl.LOAN_APP_SEQ\r\n"
            + "and la.LOAN_APP_SEQ = ins.LOAN_APP_SEQ and ins.CRNT_REC_FLG = 1\r\n" + "and la.LOAN_APP_SEQ = ldm.LOAN_APP_SEQ\r\n"
            + "and ins.HLTH_INSR_PLAN_SEQ = pln.HLTH_INSR_PLAN_SEQ and pln.CRNT_REC_FLG = 1\r\n" + "and la.PORT_SEQ = mp.PORT_SEQ\r\n"
            + "and la.LOAN_APP_STS = 703\r\n" + "and mp.BRNCH_SEQ = :brnch_cd\r\n"
            + "and to_date(ldm.DSBMT_DT) between to_date(:from_dt) and to_date(:to_dt)\r\n" + "UNION\r\n"
            + "Select distinct la.CLNT_SEQ,initcap(c.FRST_NM||' '||c.LAST_NM) client_name,\r\n"
            + "(select nvl(initcap(rel1.FRST_NM||' '||rel1.LAST_NM),'') from mw_clnt_rel rel1 where rel1.REL_WTH_CLNT_KEY IN (421,584,440,545,567,17,966,188,556,1005,434)\r\n"
            + "    and rel1.loan_app_seq = la.loan_app_seq and rel1.CRNT_REC_FLG = 1 )husband_name,\r\n"
            + "(select distinct nvl(initcap(rel1.FRST_NM||' '||rel1.LAST_NM),'')\r\n" + " from mw_clnt_rel rel1\r\n"
            + " join mw_ref_cd_val vl on vl.ref_cd_seq=rel1.rel_wth_clnt_key and vl.crnt_rec_flg=1 and vl.ref_cd='0001'\r\n"
            + "where rel1.loan_app_seq = la.loan_app_seq and rel1.CRNT_REC_FLG = 1\r\n" + ") father_name,\r\n" + "c.CNIC_NUM,\r\n"
            + "initcap(pln.PLAN_NM) plan,pln.ANL_PREM_AMT,\r\n" + "mem.HLTH_INSR_MEMB_SEQ,\r\n" + "initcap(mem.MEMBER_NM),\r\n"
            + "trunc(months_between(to_date(ldm.DSBMT_DT),TO_DATE(TO_CHAR(mem.DOB,'DDMMYYYY'),'DDMMYYYY'))/12)||' Years '||trunc(mod(months_between(to_date(ldm.DSBMT_DT),TO_DATE(TO_CHAR(mem.DOB,'DDMMYYYY'),'DDMMYYYY')),12))||' Months '|| trunc(sysdate-add_months(TO_DATE(TO_CHAR(to_date(ldm.DSBMT_DT),'DDMMYYYY'),'DDMMYYYY'),trunc(months_between(sysdate,TO_DATE(TO_CHAR(to_date(ldm.DSBMT_DT),'DDMMYYYY'),'DDMMYYYY'))/12)*12+trunc(mod(months_between(sysdate,TO_DATE(TO_CHAR(to_date(ldm.DSBMT_DT),'DDMMYYYY'),'DDMMYYYY')),12))))||' Days' Age_at_insurace_time,\r\n"
            + "(select initcap(val.REF_CD_DSCR)\r\n" + "from MW_REF_CD_VAL val\r\n" + "where val.REF_CD_SEQ = mem.MRTL_STS_KEY\r\n"
            + "and val.CRNT_REC_FLG = 1\r\n" + ")marital_status,\r\n"
            + "(select initcap(val.REF_CD_DSCR) from MW_REF_CD_VAL val where val.REF_CD_SEQ = mem.REL_KEY and val.CRNT_REC_FLG = 1 )relation,\r\n"
            + "(select initcap(val.REF_CD_DSCR) from MW_REF_CD_VAL val where val.REF_CD_SEQ = mem.GNDR_KEY and val.CRNT_REC_FLG = 1 )Gender,\r\n"
            + "trunc(to_date(ldm.EFF_START_DT))enrollment_date,\r\n" + "trunc(to_date(ldm.DSBMT_DT))insurance_date,\r\n"
            + "trunc(to_date(ldm.DSBMT_DT)+365) maturity_date,\r\n" + "hl.CARD_NUM,\r\n"
            + "(select dt_of_dth from mw_dth_rpt dr where dr.crnt_rec_flg=1 and dr.clnt_seq=c.clnt_seq) dth_dt\r\n" + "from\r\n"
            + "mw_loan_app la,\r\n" + "mw_clnt c,\r\n" + "MW_HLTH_INSR_MEMB mem,\r\n"
            + "MW_CLNT_HLTH_INSR_CARD hl,MW_HLTH_INSR_PLAN pln,\r\n" + "MW_CLNT_HLTH_INSR ins,MW_DSBMT_VCHR_HDR ldm,mw_port mp\r\n"
            + "where mem.LOAN_APP_SEQ = hl.LOAN_APP_SEQ\r\n" + "and hl.LOAN_APP_SEQ = ins.LOAN_APP_SEQ\r\n"
            + "and la.LOAN_APP_SEQ = mem.LOAN_APP_SEQ\r\n"
            + "and ins.HLTH_INSR_PLAN_SEQ = pln.HLTH_INSR_PLAN_SEQ and pln.CRNT_REC_FLG = 1\r\n"
            + "and la.CLNT_SEQ = c.CLNT_SEQ and c.CRNT_REC_FLG = 1\r\n" + "and la.LOAN_APP_STS = 703 and la.CRNT_REC_FLG = 1\r\n"
            + "and la.PORT_SEQ = mp.PORT_SEQ\r\n" + "and la.LOAN_APP_SEQ = ldm.LOAN_APP_SEQ and ldm.CRNT_REC_FLG = 1\r\n"
            + "and mem.REL_KEY in (\r\n" + "582,423,467,435,1008,569,547,189,969,557,438 ----SON\r\n"
            + ",570,437,548,1105,466,422,558,436,190,1009,970 ---DAUGHTER\r\n"
            + ",421,584,440,545,567,17,966,188,556,1005,434 --HUSBAND\r\n" + " )\r\n" + "and mp.BRNCH_SEQ = :brnch_cd\r\n"
            + "and to_date(ldm.DSBMT_DT) between to_date(:from_dt) and to_date(:to_dt)";
    public static String INSURANCE_CLAIM = "select  \r\n" + "emp.emp_nm,clnt_id,\r\n" + "    clnt.frst_nm||' '||clnt.last_nm clnt_nm,\r\n"
            + "    nom_nm,\r\n" + "    dth.dt_of_dth,\r\n" + "    case when dth.clnt_nom_flg=0 then 'SELF' else prsn end dsd_prsn,\r\n"
            + "    adj.pymt_dt adj_dt,\r\n" + "    prd.prd_cmnt,\r\n" + "    hdr.dsbmt_dt,\r\n" + "    ap.aprvd_loan_amt dsbmt_amt,\r\n"
            + "    adj.prn_amt prn_amt,\r\n" + "    adj.svc_amt srv_amt,\r\n" + "    jh.jv_dt ins_clm_dt,\r\n"
            + "    case when ap.prnt_loan_app_seq=ap.loan_app_seq then 5000 else 0 end fnrl_chrgs \r\n" + "from mw_dth_rpt dth\r\n"
            + "join mw_clnt clnt on clnt.clnt_seq=dth.clnt_seq and clnt.crnt_rec_flg=1\r\n"
            + "join mw_loan_app ap on ap.clnt_seq=clnt.clnt_seq and ap.crnt_rec_flg=1 \r\n"
            + "     and ap.loan_cycl_num=(select max(loan_cycl_num) from mw_loan_app where crnt_rec_flg=1 and clnt_seq=clnt.clnt_seq)\r\n"
            + "join mw_port prt on prt.port_seq=ap.port_seq and prt.crnt_rec_flg=1\r\n"
            + "join mw_port_emp_rel erl on erl.port_seq=ap.port_seq and erl.crnt_rec_flg=1\r\n"
            + "join mw_emp emp on emp.emp_seq=erl.emp_seq\r\n"
            + "join mw_dsbmt_vchr_hdr hdr on hdr.loan_app_seq=ap.loan_app_seq and hdr.crnt_rec_flg=1\r\n"
            + "join mw_exp fnrl on fnrl.crnt_rec_flg=1 and fnrl.exp_ref=clnt.clnt_seq and fnrl.expns_typ_seq=424\r\n"
            + "join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1\r\n"
            + "join mw_jv_hdr jh on jh.enty_seq=fnrl.exp_seq and jh.enty_typ='Expense'\r\n" + "left outer join  (\r\n"
            + "    select loan_app_seq,frst_nm||' '||last_nm nom_nm, ref_cd_dscr prsn\r\n" + "    from mw_clnt_rel nom \r\n"
            + "    join mw_ref_cd_val rwc on rwc.ref_cd_seq=nom.rel_wth_clnt_key and rwc.crnt_rec_flg=1\r\n"
            + "    where nom.crnt_rec_flg=1 \r\n" + "      and nom.rel_typ_flg=1\r\n"
            + "   ) nom on nom.loan_app_seq=ap.loan_app_seq   \r\n" + "left outer join (\r\n"
            + "    select la.loan_app_seq,trx.pymt_dt,la.prd_seq,sum(psd.PPAL_AMT_DUE) prn_amt,sum(psd.TOT_CHRG_DUE) svc_amt\r\n"
            + "    from mw_rcvry_trx trx\r\n" + "    join mw_typs rcvry on rcvry.typ_seq = rcvry_typ_seq and rcvry.crnt_rec_flg = 1 \r\n"
            + "    join mw_rcvry_dtl dtl on dtl.rcvry_trx_seq = trx.rcvry_trx_seq and dtl.crnt_rec_flg = 1 \r\n"
            + "    join mw_pymt_sched_dtl psd on psd.pymt_sched_dtl_seq = dtl.pymt_sched_dtl_seq and psd.crnt_rec_flg = 1 \r\n"
            + "    join mw_pymt_sched_hdr psh on psd.pymt_sched_hdr_seq = psh.pymt_sched_hdr_seq and psh.crnt_rec_flg = 1 \r\n"
            + "    join mw_loan_app la on la.loan_app_seq = psh.loan_app_seq and la.crnt_rec_flg = 1 \r\n"
            + "    join mw_ref_cd_val val on val.ref_cd_seq = la.loan_app_sts and val.crnt_rec_flg = 1 and val.del_flg = 0 \r\n"
            + "    where trx.post_flg = 1 and trx.crnt_rec_flg = 1 and rcvry.typ_id ='0020' \r\n" + "    and dtl.chrg_typ_key=-1\r\n"
            + "    group by la.loan_app_seq,trx.pymt_dt,la.prd_seq\r\n"
            + ") adj on adj.loan_app_seq=ap.loan_app_seq and adj.prd_seq=ap.prd_seq\r\n" + "where dth.crnt_rec_flg=1\r\n"
            + " and (to_date(jh.jv_dt) between to_date(:from_dt) and to_date(:to_dt) or to_date(pymt_dt) between to_date(:from_dt) and to_date(:to_dt)) and prt.brnch_seq=:brnch_cd";
    public static String FIVE_DAYS_ADVANCE_RECOVERY_TRENDS = "select emp_nm,      clnt_id,clnt_nm, due_dt,       pymt_dt, \r\n"
            + "    due_dt-pymt_dt adv_days,      ph_num   from mw_pymt_sched_hdr psh  \r\n"
            + "join mw_pymt_sched_dtl psd on psd.pymt_sched_hdr_seq=psh.pymt_sched_hdr_seq and psd.crnt_rec_flg=1   join (  \r\n"
            + "    select emp.emp_nm,rd.pymt_sched_dtl_seq,trx.pymt_dt,clnt_id,frst_nm||' '||last_nm clnt_nm,clnt.ph_num  \r\n"
            + "    from mw_rcvry_dtl rd  \r\n"
            + "    join mw_rcvry_trx trx on trx.rcvry_trx_seq=rd.rcvry_trx_seq and trx.crnt_rec_flg=1  \r\n"
            + "    join mw_pymt_sched_dtl psd on psd.pymt_sched_dtl_seq=rd.pymt_sched_dtl_seq and psd.crnt_rec_flg=1  \r\n"
            + "    join mw_pymt_sched_hdr psh on psh.pymt_sched_hdr_seq = psd.pymt_sched_hdr_seq and psh.crnt_rec_flg=1  \r\n"
            + "    join mw_loan_app ap on ap.loan_app_seq=psh.loan_app_seq and ap.crnt_rec_flg=1  \r\n"
            + "    join mw_port_emp_rel erl on erl.port_seq=ap.port_seq and erl.crnt_rec_flg=1  \r\n"
            + "    join mw_port mp on mp.port_seq=ap.port_seq and mp.crnt_rec_flg=1 and mp.brnch_seq=:brnchSeq  \r\n"
            + "    join mw_emp emp on emp.emp_seq=erl.emp_seq  \r\n"
            + "    join mw_clnt clnt on clnt.clnt_seq=ap.clnt_seq and clnt.crnt_rec_flg=1  \r\n"
            + "    join mw_prd prd on prd.prd_seq=ap.prd_seq and prd.crnt_rec_flg=1      where rd.crnt_rec_flg=1 \r\n"
            + "    group by emp.emp_nm,rd.pymt_sched_dtl_seq,trx.pymt_dt,clnt_id,frst_nm||' '||last_nm,clnt.ph_num  \r\n"
            + ") paid on paid.pymt_sched_dtl_seq=psd.pymt_sched_dtl_seq   where psh.crnt_rec_flg=1  \r\n"
            + "and due_dt-pymt_dt >5 and pymt_dt between to_date(:fromDt,'dd-mm-yyyy') and to_date(:toDt,'dd-mm-yyyy')  \r\n"
            + "order by 1,adv_days desc";
    public static String verisysBmApprovalFunc = "select fn_verisys_bm_approval (:P_LOAN_APP_SEQ) from dual";

    public static String NADI_BY_LOAN_APP_SEQ = "select ndi from vw_loan_app where loan_app_seq=:loanAppSeq";

    public static String IS_APPRAISAL_BY_LOAN_APP_SEQ = "SELECT REL.FORM_SEQ FROM MW_LOAN_APP APP\n" +
            "    JOIN MW_PRD_FORM_REL REL ON REL.PRD_SEQ = APP.PRD_SEQ AND REL.CRNT_REC_FLG =1 AND REL.FORM_SEQ IN (7,14,15) \n" +
            "    WHERE APP.LOAN_APP_SEQ = :loanAppSeq AND APP.CRNT_REC_FLG = 1";

    /*
     *  Added By Naveed - Date - 24-02-2022
     *  Upaisa and HBL Konnect Mobile wallet Payment Mode
     * ATM Cards Management Screen
     * */
    public static String ATM_CARDS_MANAGEMENT_SCRIPT = "SELECT \n" +
            "       emp.emp_nm bdo,\n" +
            "       c.CLNT_ID,\n" +
            "       c.frst_nm ||' '|| c.last_nm clnt_nm,\n" +
            "       c.cnic_num,\n" +
            "       mob.MOB_WAL_NO, \n" +
            "       mob.ATM_CARD_NO,\n" +
            "       mob.ATM_CARD_REC_DT,\n" +
            "       mob.ATM_CARD_DELVRY_DT,\n" +
            "       mob.MOB_WAL_SEQ\n" +
            "  FROM mw_loan_app  l\n" +
            "       JOIN mw_dsbmt_vchr_hdr hdr on hdr.LOAN_APP_SEQ = l.LOAN_APP_SEQ and hdr.CRNT_REC_FLG = 1\n" +
            "       JOIN mw_dsbmt_vchr_dtl dtl on dtl.DSBMT_HDR_SEQ = hdr.DSBMT_HDR_SEQ and dtl.CRNT_REC_FLG = 1\n" +
            "       JOIN mw_typs typ on typ.TYP_SEQ = dtl.PYMT_TYPS_SEQ and typ.crnt_rec_flg = 1 and  typ.typ_id  in ('0019')  \n" +
            "       JOIN mw_mob_wal_info mob on mob.MOB_WAL_SEQ =  hdr.MOB_WAL_SEQ and mob.CRNT_REC_FLG = 1\n" +
            "       JOIN mw_clnt c ON c.clnt_seq = l.clnt_seq AND c.crnt_rec_flg = 1\n" +
            "       JOIN mw_port po ON po.port_seq = l.port_seq AND po.crnt_rec_flg = 1\n" +
            "       JOIN mw_port_emp_rel poer\n" +
            "           ON poer.port_seq = po.port_seq AND poer.crnt_rec_flg = 1\n" +
            "       JOIN mw_emp emp ON emp.emp_seq = poer.emp_seq\n" +
            "       JOIN mw_brnch brnch\n" +
            "           ON     brnch.brnch_seq = po.brnch_seq\n" +
            "              AND brnch.brnch_seq = :brnch_seq\n" +
            "              AND brnch.crnt_rec_flg = 1\n" +
            " WHERE l.crnt_rec_flg = 1 and l.loan_app_sts = 703 ";

    public static String ATM_CARDS_MANAGEMENT_COUNT_SCRIPT = "SELECT \n" +
            "       count(c.CLNT_ID)\n" +
            "  FROM mw_loan_app  l\n" +
            "       JOIN mw_dsbmt_vchr_hdr hdr on hdr.LOAN_APP_SEQ = l.LOAN_APP_SEQ and hdr.CRNT_REC_FLG = 1\n" +
            "       JOIN mw_dsbmt_vchr_dtl dtl on dtl.DSBMT_HDR_SEQ = hdr.DSBMT_HDR_SEQ and dtl.CRNT_REC_FLG = 1\n" +
            "       JOIN mw_typs typ on typ.TYP_SEQ = dtl.PYMT_TYPS_SEQ and typ.crnt_rec_flg = 1 and  typ.typ_id  in ('0019')  \n" +
            "       JOIN mw_mob_wal_info mob on mob.MOB_WAL_SEQ =  hdr.MOB_WAL_SEQ and mob.CRNT_REC_FLG = 1\n" +
            "       JOIN mw_clnt c ON c.clnt_seq = l.clnt_seq AND c.crnt_rec_flg = 1\n" +
            "       JOIN mw_port po ON po.port_seq = l.port_seq AND po.crnt_rec_flg = 1\n" +
            "       JOIN mw_port_emp_rel poer\n" +
            "           ON poer.port_seq = po.port_seq AND poer.crnt_rec_flg = 1\n" +
            "       JOIN mw_emp emp ON emp.emp_seq = poer.emp_seq\n" +
            "       JOIN mw_brnch brnch\n" +
            "           ON     brnch.brnch_seq = po.brnch_seq\n" +
            "              AND brnch.brnch_seq = :brnch_seq\n" +
            "              AND brnch.crnt_rec_flg = 1\n" +
            " WHERE l.crnt_rec_flg = 1 and l.loan_app_sts = 703 ";
}
