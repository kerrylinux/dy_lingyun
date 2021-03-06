package com.business.pound.service.impl;


import com.business.pound.entity.PoundEntity;
import com.business.pound.entity.TransportEnetity;
import com.business.pound.repository.PoundRepository;
import com.business.pound.repository.TransportRepository;
import com.business.pound.service.TransportService;
import com.business.pound.util.OrderCodeFactory;
import com.business.pound.util.PoundEnum;
import com.business.pound.util.TransportEnum;
import com.business.pound.vo.PoundTransVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import com.lingyun.core.exception.ValidateCodeException;
@Service
public class TransportServiceImpl implements TransportService {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private TransportRepository transportRepository;
    @Autowired
    private PoundRepository poundRepository;

    @Override
    public TransportEnetity save(TransportEnetity transportEnetity) {
        return transportRepository.saveAndFlush(transportEnetity);
    }

    @Override
    public void delete(TransportEnetity transportEnetity) {
        transportRepository.delete(transportEnetity);
    }

    @Override
    public List<TransportEnetity> getAll() {
        return transportRepository.findAll();
    }

    @Override
    public Page<TransportEnetity> getPage(Example<TransportEnetity> example, Pageable pageable) {

        return transportRepository.findAll(example,pageable);
    }


    public TransportEnetity getOne(Long id) {

        Optional<TransportEnetity> transportEnetity=transportRepository.findById(id);

        return transportEnetity.get();
    }

    @Override
    public void deleteById(Long id) {
        transportRepository.deleteById(id);
    }

    @Override
    public Page<PoundTransVo> getPageTransport(String transportNum, Pageable pageable) {

        if(StringUtils.isEmpty(transportNum)){

            return transportRepository.findAllTransport(pageable);
        }
        return transportRepository.findAllTransport(transportNum,pageable);
    }

    @Override
    public List<PoundTransVo> findAllList(String transportNum) {

         if(org.apache.commons.lang3.StringUtils.isEmpty(transportNum)){
             transportRepository.findAllList();
         }
        return transportRepository.findAllList(transportNum);
    }

    @Override
    @Transactional
    public int apporval(String[] ids, String status) {
        String cartNum="";
        TransportEnetity transportEnetity=new TransportEnetity();
        String transNum=OrderCodeFactory.getTransCode(1L);
        double  weight=0.0;//??????
        double  tareWeight=0.0;//??????
        //double  netWeight=0.0;//??????

         for(int i=0;i<ids.length;i++){

             Long idV=Long.valueOf(ids[i]);
             //transportEnetity.setPoundId(idV);

             PoundEntity poundEntity=poundRepository.getOne(idV);

             if(null==poundEntity ||null== poundEntity.getId()){//???????????????

                 continue;
             }
             if(StringUtils.isEmpty(cartNum)){
                 cartNum=poundEntity.getCarNum();
             }else {
                 if(!cartNum.equals(poundEntity.getCarNum())){//????????????????????????????????????

                     throw new ValidateCodeException("???????????????????????????????????????");
                     //continue;
                 }
             }
             poundEntity.setPoundStatus(PoundEnum.valueOf(status));//????????????
             poundEntity.setTransportNum(transNum);

             transportEnetity.setGoodsName(poundEntity.getGoodsName());//????????????
             transportEnetity.setReciveUnit(poundEntity.getReciveUnit());//????????????
             transportEnetity.setDeliverUnit(poundEntity.getDeliverUnit());//????????????
             transportEnetity.setCarNum(poundEntity.getCarNum());//?????????
             if(null==poundEntity.getFlowTo()){
                 transportEnetity.setFlowTo(PoundEnum.OPT_E);
             }else {
                 transportEnetity.setFlowTo(poundEntity.getFlowTo());//????????????
             }

             if(ids.length==1){

                 transportEnetity.setWeight(poundEntity.getWeight());//??????
                 transportEnetity.setTareWeight(poundEntity.getTareWeight());//??????
                 transportEnetity.setNetWeight(poundEntity.getWeight()-poundEntity.getTareWeight());//??????
             }else {
                 if(i==0){//???????????????
                     if(null!=poundEntity.getWeight()){//??????
                         weight=poundEntity.getWeight();
                     }
                     if(null != poundEntity.getTareWeight()){//??????
                         tareWeight=poundEntity.getTareWeight();
                     }
                 }
                  if(i>0){//???????????????
                      /******
                       * ???????????????????????????????????????????????????????????????????????????????????????????????????
                       * ???????????????-?????????????????????????????????
                       * ??????????????????????????????????????????[?????????????????????]???
                       * ????????????????????????????????????????????????????????????????????????
                       * ???????????????????????????????????????????????????????????????????????????*
                       * *********/
                      if(weight>poundEntity.getWeight()){
                          transportEnetity.setWeight(weight);//??????

                      }else {
                          transportEnetity.setWeight(poundEntity.getWeight());//??????

                      }

                      //??????????????????
                      if(tareWeight>poundEntity.getTareWeight()){

                          transportEnetity.setTareWeight(tareWeight);//??????
                      } else {

                          transportEnetity.setTareWeight(poundEntity.getTareWeight());

                      }
                      //??????????????????????????????0?????????????????????????????????????????????????????????????????????
                      if(transportEnetity.getTareWeight()==0){
                          if(weight>poundEntity.getWeight()){
                              transportEnetity.setTareWeight(poundEntity.getWeight());
                          }else {
                              transportEnetity.setTareWeight(weight);
                          }

                      }
                      transportEnetity.setNetWeight(transportEnetity.getWeight()-transportEnetity.getTareWeight());//??????
                  }
             }
             transportEnetity.setPoundAccount(poundEntity.getPoundAccount());
        }
        //transportEnetity.setPoundNum(poundEntity.getPoundNum());


        transportEnetity.setStatus(TransportEnum.A);

        transportEnetity.setTransportNum(transNum);

        transportRepository.save(transportEnetity);
        return 1;
    }

    @Override
    public Page<TransportEnetity> findAll(String transportNum, Pageable pageable) {
         if(StringUtils.isEmpty(transportNum)){
             return transportRepository.findAll(pageable);
         }
        return transportRepository.findAllByTransportNum(transportNum,pageable);

    }
}
