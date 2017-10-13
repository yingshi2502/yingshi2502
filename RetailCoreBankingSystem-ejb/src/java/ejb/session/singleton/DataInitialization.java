/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;

/**
 *
 * @author yingshi
 */
@Singleton
public class DataInitialization implements DataInitializationLocal {

    @PostConstruct
    public void PostConstruct(){
        // direct to data initialzation
    }
    
}
