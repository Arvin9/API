package site.nebulas.service;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import site.nebulas.beans.Response;

import java.io.*;


@Service
public class JudgeService {
	private Logger logger = LoggerFactory.getLogger(JudgeService.class);

	private String errorMsg;

	public Response judge(String content){
		Response response = new Response();

		//1、将内容写入.c文本
		String tmpdir = System.getProperty("java.io.tmpdir");
		if("/".equals(File.separator)){
			tmpdir += "/";
		}
		writeTxtFile("test.c",content);
		//2、调用gcc命令进行编译
		String[] command = new String[]{"gcc",tmpdir+"test.c","-o",tmpdir+"test.exe"};
		if( compile(command) ){
			response.setRet(200);
			response.setMsg("编译成功");

			//3、执行.exe返回结果
			String[] exec = new String[]{tmpdir+"test.exe"};
			response.setData(execute(exec));
		}else {
			response.setRet(400);
			response.setMsg("编译失败:"+errorMsg);
		}
		return response;
	}

	//1、将内容写入.c文本
	public String writeTxtFile(String filename,String str){
		String content = "";
		content += "#include<stdio.h>\n";
		content += "void main(){";
		content += str;
		content += "}";
		logger.info("content" + content);
		String tmpdir = System.getProperty("java.io.tmpdir");
		if("/".equals(File.separator)){
			tmpdir += "/";
		}
		String filePath = tmpdir + filename;
		logger.info(".c文件路径" + filePath);
		try {
			File file = new File(filePath);
			if (!file.exists()){
				file.createNewFile();
			}
			OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file),"utf-8");
			BufferedWriter writer = new BufferedWriter(write);
			writer.write(content);
			writer.close();
		} catch (Exception e){
			e.printStackTrace();
			logger.error("写入文本异常");
		}
		return filePath;
	}
	//2、调用gcc命令进行编译
	public boolean compile(String[] command){
		boolean flag = true;
		Runtime rt = Runtime.getRuntime();
		Process process = null;
		try {
			process = rt.exec(command);
			int exitVal = process.waitFor(); // 阻塞当前线程，并等待外部程序中止后获取结果码

			String s;
			BufferedReader bufferedReader;
			if(exitVal == 0){
				logger.info("编译成功");
			}else {
				flag = false;
				logger.info("编译失败");

				bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(),"utf-8"));
				errorMsg = bufferedReader.readLine();
				while((s=bufferedReader.readLine()) != null)
					errorMsg += "\n" + s ;
				logger.info("compile_error: " + errorMsg);
			}

			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"utf-8"));
			logger.info("compile_input: "+bufferedReader.readLine());
			while((s=bufferedReader.readLine()) != null)
				logger.info(s);

			BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(process.getOutputStream(),"utf-8"));
			logger.info("compile_output: "+bufferedReader.readLine());
			while((s=bufferedReader.readLine()) != null)
				logger.info(s);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return flag;
	}
	//3、执行.exe返回结果
	public String execute(String[] command){
		Runtime rt = Runtime.getRuntime();
		Process process = null;
		String result = "";
		try {
			process = rt.exec(command);

			String s;
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream(),"utf-8"));
			logger.info("execute_error: "+bufferedReader.readLine());
			while((s=bufferedReader.readLine()) != null)
				logger.info(s);

			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream(),"utf-8"));
			result = bufferedReader.readLine();
			while((s=bufferedReader.readLine()) != null)
				result += "\n" + s ;
			logger.info("execute_input: "+ result);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

}
