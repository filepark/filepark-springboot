package service;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import mapper.AccessLogMapper;

@Service
@AllArgsConstructor
public class AccessLogService {
	final AccessLogMapper accessLogMapper;
}
