package in.mahesh.policies;

import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;

public class CustomSkipPolicy implements SkipPolicy{

	@Override
	public boolean shouldSkip(Throwable t, long skipCount) throws SkipLimitExceededException {
		// TODO Auto-generated method stub
		System.out.println("Skip Policy Excuted");
		if(t instanceof NumberFormatException || t instanceof StringIndexOutOfBoundsException ) {
			return true;
		}
		return false;
	}

}
