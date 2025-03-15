package com.web.did_test;

import com.web.did_test.utils.EncryptUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest
public class EncryptTest {


    @Test
    public  void testEncrypt() {
        // 假设加密时的参数
        long originalTimestamp = 1741243763464L;
        String iv = "AuN3Pxgs//djzzyZJqNvYw==";  // 替换为实际IV
        String encrypted = "19gkGob43geDNJ4hbP979kFTqMZKf6kXqn9I5pYE+N1cOkKspU//9YDlisam12zQKwuai/QDFbVLeZnjx9LCkYEEQa9CXX6O6mCqrA+owMI+0/URMaG+lOWq8CVgoJaaDueSDkIdnqAeRDgDB34s36l0d4+ALjz3QCWNosfKa8SCOgwqqKgQsEZ9CtQPKF3pnOrH5aiV1J6tCzP2FioWhJUiure0FJZiIJh/ZlJwC8rOUuHVgH9zDq7mP2pm44La3LO0Xaz+1GE/Oe116HR6uioJYbVlbWKu3E8OItWWlra1fh33G1Pz1fFqvKaKQDUnhQuhZWyJeR1bTUXTdmZoW/kpmOCjTQd6Fin6HBDAdkYk/CB4MknaOIxyG/gtiwxn+fx+NIg7zW6za5SlXHWEUm4nL4vdc5yVLvJR/QJKtzXNywEnt06vp107DNEA0UrzrDxgP3EAU1waxSwco3LazLNK6FyMoKMR0ShIEyclCD5M67ymVo/xQc/AqFR8XFznsUnFAdF8b/f0dc5B/o7qTJeE4qc1WfXtObbDFtYzqNzUTj3EksvWLxKJgfepUVi6HOpFW5ddksqIRxP6gfWEJgZruAZ7oyYeINXu0sPnqHk1/ulIOokaPjZFHYQjIrpmUmupVoVkcWs3cpBZnN6hvGnfTDhUaIDZHL8wyhMBS8osebW5S50ul0b4h+p5+TBV03f4lFIdS+BA/JO1NynxZSg194etTTq4iMKY1QuK5V9BExfWYFcBMfyb7sLOlVjGQf/1b4IW4C7dTu27IUASICm1r7WbY/XOsz4L8Xw1eQUJdGcKZuTxub9ox3SMRJXGjLZdDCYoA03ZijeaYpxRNQL7tvodFxtqYZEshtJ6jINpU23fy+MtQQNu4y6F7x/z1iIhxhxFsk+hq+GqkbkWmRm9yHG3cD5KNmMIp7Mhmq1Z+vnsvUtbRHhmEqE53L/QG96J5sXo+Ba3k7ryYrKhUkVrffzMpt7hyT0hbPq2+RaqaeELr75HnD3YpriluRFbjz6WoHSMnJLYLcmnsFEbqcUfs0u4Qiw+oAKb95mTPLkWfX35g2VC7yky5r6R5V7iG665h5kqLnky/QkAXYGm+McZFwYRVeOBVjVAnIcyGFBdfh2kLeBB8hvPOLK0uxiBCKXV0DsRwXnw70YuKdvCS8GL562CqUuANDLjvfRecHBmi2Zh/8rYZ4iZXe4tNshFtmZ7r8GwxYjJuigH2m7HkO0Z8GWckm30+aArwocPt4h4L9PAcDU3LYeoKWA3t0gYdkzCEe5BaPrQQo45dnUsItD+v8vFwVDM5+6s6s3YjVwHASEUIfU0mSyoSR001mXaNk57IXhcEo8OEnk1NrwG7/GfrwOyn8Dw2PNOKVv1qxSXIiRhMRXBwe8mlxm40Jy2a8FfBD1l78R8zeuTEDtb5TmiWXFOGDdcCjjpoN8AUh0AP/JRQUfzwG/mCuIqwyc3t4dZk9CL09eAazs8efFFD5EJHZbQpp3uFP+vTCkp3CIVOmUzna8KdB5DVxXWvnWHBV3I3o09cRs4CdN4V3XMYDBdUwkUH8EmXwnBsSscn7g=";

        // 执行解密
        String decrypted = null;
        try {
            decrypted = EncryptUtil.decrypt(iv, encrypted, originalTimestamp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("解密结果: " + decrypted);
    }
}
