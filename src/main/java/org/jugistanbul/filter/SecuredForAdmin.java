package org.jugistanbul.filter;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author hakdogan (huseyin.akdogan@patikaglobal.com)
 * Created on 16.07.2021
 **/
@NameBinding
@Retention(RetentionPolicy.RUNTIME)
public @interface SecuredForAdmin
{
}
